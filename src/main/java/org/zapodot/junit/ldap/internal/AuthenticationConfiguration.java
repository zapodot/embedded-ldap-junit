package org.zapodot.junit.ldap.internal;

import javax.naming.Context;
import java.util.HashMap;
import java.util.Map;

/**
 * LDAP authentication POJO.
 *
 * This class is part of the internal API and may thus be changed or removed without warning.
 */
public class AuthenticationConfiguration {
    public final String userDn;
    public final String credentials;

    public AuthenticationConfiguration(final String userDn, final String credentials) {
        this.userDn = userDn;
        this.credentials = credentials;
    }


    public Map<String, String> toAuthenticationEnvironment() {
        final HashMap<String, String> authenticationConfiguration = new HashMap<>();
        if (userDn != null && credentials != null) {
            authenticationConfiguration.put(Context.SECURITY_PRINCIPAL, userDn);
            authenticationConfiguration.put(Context.SECURITY_PROTOCOL, "simple");
            authenticationConfiguration.put(Context.SECURITY_CREDENTIALS, credentials);
        } else {
            authenticationConfiguration.put(Context.SECURITY_PROTOCOL, "none");
        }
        return authenticationConfiguration;
    }
}
