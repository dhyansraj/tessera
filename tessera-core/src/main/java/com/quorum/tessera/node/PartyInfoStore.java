package com.quorum.tessera.node;

import com.quorum.tessera.core.config.ConfigService;
import com.quorum.tessera.node.model.Party;
import com.quorum.tessera.node.model.PartyInfo;
import com.quorum.tessera.node.model.Recipient;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Stores a list of all discovered nodes and public keys
 */
public class PartyInfoStore {

    private final String advertisedUrl;

    private final Set<Recipient> recipients;

    private final Set<Party> parties;

    public PartyInfoStore(final ConfigService configService) {

        //TODO: remove the extra "/" when we deprecate backwards compatibility
        this.advertisedUrl = configService.getServerUri().toString() + "/";

        this.recipients = new HashSet<>();
        this.parties = new HashSet<>();
        this.parties.add(new Party(this.advertisedUrl));
    }

    /**
     * Merge an incoming {@link PartyInfo} into the current one, adding any
     * new keys or parties to the current store
     * @param newInfo the incoming information that may contain new nodes/keys
     */
    public synchronized void store(final PartyInfo newInfo) {
        recipients.addAll(newInfo.getRecipients());
        parties.addAll(newInfo.getParties());
    }

    /**
     * Fetch a copy of all the currently discovered nodes/keys
     *
     * @return an immutable copy of the current state of the store
     */
    public synchronized PartyInfo getPartyInfo() {
        return new PartyInfo(
            advertisedUrl,
            unmodifiableSet(new HashSet<>(recipients)),
            unmodifiableSet(new HashSet<>(parties))
        );
    }

}
