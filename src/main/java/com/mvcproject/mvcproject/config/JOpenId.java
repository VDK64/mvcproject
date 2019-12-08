package com.mvcproject.mvcproject.config;

import org.expressme.openid.Association;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

public class JOpenId {
    private static String url;

    static {
        OpenIdManager manager = new OpenIdManager();
        manager.setReturnTo("http://localhost:8090/settings");
//        manager.setRealm(""); do not need until localhost
        Endpoint endpoint = manager.lookupEndpoint("https://steamcommunity.com/openid/");
        Association association = manager.lookupAssociation(endpoint);
        url = manager.getAuthenticationUrl(endpoint, association);
    }

    public static String getUrl() {
        return url;
    }
}
