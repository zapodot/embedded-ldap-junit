package org.zapodot.junit.ldap;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchScope;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmbeddedLdapRuleClassRuleTest {
    public static final String DOMAIN_DSN = "dc=zapodot,dc=org";

    @ClassRule
    public static EmbeddedLdapRule embeddedLdapRule = EmbeddedLdapRuleBuilder
            .newInstance()
            .usingDomainDsn(DOMAIN_DSN)
            .usingBindDSN("cn=Directory manager")
            .usingBindCredentials("testPass")
            .importingLdifs("example.ldif")
            .build();

    @Test
    public void testCheck() throws Exception {
        final LDAPConnection ldapConnection = embeddedLdapRule.ldapConnection();
        assertEquals(4, ldapConnection.search(DOMAIN_DSN, SearchScope.SUB, "(objectClass=*)").getEntryCount());

    }
}