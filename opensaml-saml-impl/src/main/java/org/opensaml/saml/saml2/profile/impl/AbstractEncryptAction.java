/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml2.profile.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.logic.FunctionSupport;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.saml.saml2.encryption.Encrypter.KeyPlacement;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Abstract base class for actions that perform simple unicast SAML encryption to a single
 * decrypting party.
 * 
 * <p>The {@link EncryptionContext} governing the encryption process is located by a lookup
 * strategy, by default a child of the outbound message context.</p>
 * 
 * <p>An optional recipient name is also obtained from a lookup strategy.</p> 
 */
public abstract class AbstractEncryptAction extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractEncryptAction.class);

    /** Strategy used to locate the {@link EncryptionContext}. */
    @Nonnull private Function<ProfileRequestContext,EncryptionContext> encryptionCtxLookupStrategy;
    
    /** Strategy used to locate the encryption recipient. */
    @Nullable private Function<ProfileRequestContext,String> recipientLookupStrategy;
    
    /** Strategy used to determine encrypted key placement. */
    @Nonnull private Function<ProfileRequestContext,Encrypter.KeyPlacement> keyPlacementLookupStrategy;
    
    /** Predicate used to determine whether to perform encrypt-to-self. */
    @Nonnull private Predicate<ProfileRequestContext> encryptToSelf;
    
    /** Strategy used to resolve the encrypt-to-self parameters. */
    @Nullable private Function<Pair<ProfileRequestContext, EncryptionParameters>, 
        List<EncryptionParameters>> encryptToSelfParametersStrategy;
    
    /** Strategy used to obtain the self recipient value. */
    @Nullable private Function<ProfileRequestContext, String> selfRecipientLookupStrategy;
    
    /** The encryption object. */
    @Nullable private Encrypter encrypter;
    
    /** Constructor. */
    public AbstractEncryptAction() {
        encryptionCtxLookupStrategy = Functions.compose(new ChildContextLookup<>(EncryptionContext.class),
                new OutboundMessageContextLookup());
        keyPlacementLookupStrategy = FunctionSupport.<ProfileRequestContext,KeyPlacement>constant(KeyPlacement.INLINE);
        encryptToSelf = Predicates.alwaysFalse();
    }
    
    /**
     * Set the strategy used to locate the {@link EncryptionContext} associated with a given
     * {@link ProfileRequestContext}.
     * 
     * @param strategy lookup strategy
     */
    public void setEncryptionContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,EncryptionContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        encryptionCtxLookupStrategy =
                Constraint.isNotNull(strategy, "EncryptionContext lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the encryption recipient.
     * 
     * @param strategy lookup strategy
     */
    public void setRecipientLookupStrategy(@Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        recipientLookupStrategy = Constraint.isNotNull(strategy, "Recipient lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to determine the encrypted key placement strategy.
     * 
     * @param strategy  lookup strategy
     */
    public void setKeyPlacementLookupStrategy(@Nonnull final Function<ProfileRequestContext,KeyPlacement> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        keyPlacementLookupStrategy = Constraint.isNotNull(strategy, "Key placement lookup strategy cannot be null");
    }
    
    /**
     * Set the predicate used to determine whether to perform encrypt-to-self.
     * 
     * @param predicate the encrypt-to-self predicate
     */
    public void setEncryptToSelf(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        encryptToSelf = Constraint.isNotNull(predicate, "Encrypt-to-self predicate cannot be null");
    }
    
    /**
     * Set the strategy used to resolve the encrypt-to-self parameters. 
     * 
     * @param strategy the encrypt-to-self predicate
     */
    public void setEncryptToSelfParametersStrategy(
            @Nullable final Function<Pair<ProfileRequestContext, EncryptionParameters>, 
            List<EncryptionParameters>> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        encryptToSelfParametersStrategy = strategy;
    }
    
    /**
     * Set the strategy used to locate the self identity value to use.
     * 
     * @param strategy lookup strategy
     */
    public void setSelfRecipientLookupStrategy(@Nullable final Function<ProfileRequestContext, String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        selfRecipientLookupStrategy = strategy;
    }
    
    /**
     * Get the encrypter.
     * 
     * @return  the encrypter
     */
    @Nullable public Encrypter getEncrypter() {
        return encrypter;
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        final EncryptionParameters params =
                getApplicableParameters(encryptionCtxLookupStrategy.apply(profileRequestContext));
        if (params == null) {
            log.debug("{} No encryption parameters, nothing to do", getLogPrefix());
            return false;
        }
        
        final String recipient = recipientLookupStrategy != null
                ? recipientLookupStrategy.apply(profileRequestContext) : null; 
        final DataEncryptionParameters dataParams = new DataEncryptionParameters(params);
        final List<KeyEncryptionParameters> keyParams = new ArrayList<>();
        keyParams.add(new KeyEncryptionParameters(params, recipient));
        
        if (encryptToSelf.apply(profileRequestContext)) {
            log.debug("{} Encryption to self was indicated", getLogPrefix());
            String selfRecipient = null;
            if (selfRecipientLookupStrategy != null) {
                selfRecipient = selfRecipientLookupStrategy.apply(profileRequestContext);
                log.debug("{} Resolved self-encryption recipient value: {}", getLogPrefix(), selfRecipient);
            }
            if (encryptToSelfParametersStrategy != null) {
                List<EncryptionParameters> selfParams = encryptToSelfParametersStrategy.apply(
                        new Pair<ProfileRequestContext, EncryptionParameters>(profileRequestContext, params));
                if (selfParams != null && !selfParams.isEmpty()) {
                    log.debug("{} Saw {} self-encryption parameters", getLogPrefix(), selfParams.size());
                    for (EncryptionParameters selfParam : selfParams) {
                        keyParams.add(new KeyEncryptionParameters(selfParam, selfRecipient));
                    }
                } else {
                    log.error("{} Self-encryption self was indicated, but no parameters were resolved",
                            getLogPrefix());
                    ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_ENCRYPT);
                    return false;
                }
            } else {
                log.error("{} Self-encryption was indicated, but no parameters strategy was supplied", 
                        getLogPrefix());
                ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_ENCRYPT);
                return false;
            }
        }
        
        encrypter = new Encrypter(dataParams, keyParams);
        encrypter.setKeyPlacement(keyPlacementLookupStrategy.apply(profileRequestContext));
        
        return true;
    }
    
    /**
     * Return the right set of parameters for the operation to be performed, or none if no encryption should occur.
     * 
     * @param ctx   possibly null input context to pull parameters from
     * 
     * @return  the right parameter set, or null for none
     */
    @Nullable protected abstract EncryptionParameters getApplicableParameters(@Nullable final EncryptionContext ctx);
    
}