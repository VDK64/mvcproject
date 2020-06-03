package com.mvcproject.mvcproject.config;

import lombok.Getter;
import org.expressme.openid.Association;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JOpenId {
    private final String url;

    {
        OpenIdManager manager = new OpenIdManager();
        manager.setReturnTo("http://localhost:8090/settings");
//        manager.setRealm(""); do not need until localhost
        Endpoint endpoint = manager.lookupEndpoint("https://steamcommunity.com/openid/");
        Association association = manager.lookupAssociation(endpoint);
        url = manager.getAuthenticationUrl(endpoint, association);
    }
}
