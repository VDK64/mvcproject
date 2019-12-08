package com.mvcproject.mvcproject.config;

import org.expressme.openid.Association;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

public class JOpenId {
    private static final OpenIdManager manager = new OpenIdManager();
    private static Association association;
    private static String url;

    static {
        manager.setReturnTo("http://localhost:8090/steam/login");
//        manager.setRealm(""); do not need until localhost
        Endpoint endpoint = manager.lookupEndpoint("https://steamcommunity.com/openid/");
        association = manager.lookupAssociation(endpoint);
        url = manager.getAuthenticationUrl(endpoint, association);
    }

    public static String getUrl() {
        return url;
    }
}
