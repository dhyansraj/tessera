package com.quorum.tessera.client;

import com.quorum.tessera.config.AppType;
import com.quorum.tessera.config.CommunicationType;
import com.quorum.tessera.config.Config;
import com.quorum.tessera.jaxrs.client.ClientFactory;
import com.quorum.tessera.ssl.context.ClientSSLContextFactory;
import com.quorum.tessera.ssl.context.SSLContextFactory;
import javax.ws.rs.client.Client;

public class RestP2pClientFactory implements P2pClientFactory {

    @Override
    public P2pClient create(Config config) {

        SSLContextFactory clientSSLContextFactory  = 
                ClientSSLContextFactory.create();
        
        ClientFactory clientFactory = new ClientFactory(clientSSLContextFactory);

        
        Client client = config.getServerConfigs().stream()
            .filter(c -> c.getApp() == AppType.P2P)
            .map(clientFactory::buildFrom)
            .findAny()
            .orElse(
                config.getServerConfigs().stream()
                    .findAny()
                        .map(clientFactory::buildFrom)
                        .get()
            );

        PostDelegate postDelegate = new PostDelegate(client);

        return new RestP2pClient(postDelegate);
        
    }

    @Override
    public CommunicationType communicationType() {
        return CommunicationType.REST;
    }

}
