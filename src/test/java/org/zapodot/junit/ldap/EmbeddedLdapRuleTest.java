package org.zapodot.junit.ldap;

import com.google.common.collect.Iterators;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;
import org.junit.Rule;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmbeddedLdapRuleTest {

    public static final String DOMAIN_DSN = "dc=zapodot,dc=org";
    @Rule
    public EmbeddedLdapRule embeddedLdapRule = EmbeddedLdapRuleBuilder
            .newInstance()
            .usingDomainDsn(DOMAIN_DSN)
            .importingLdifs("example.ldif")
            .build();

    @Test
    public void testLdapConnection() throws Exception {
        final LDAPConnection ldapConnection = embeddedLdapRule.ldapConnection();
        final SearchResult searchResult = ldapConnection.search(DOMAIN_DSN, SearchScope.SUB, "(objectClass=person)");
        assertEquals(1, searchResult.getEntryCount());
    }

    @Test
    public void testInitialDirContext() throws Exception {
        final InitialDirContext initialDirContext = embeddedLdapRule.initialDirContext();
        final SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        final NamingEnumeration<javax.naming.directory.SearchResult> resultNamingEnumeration =
                initialDirContext.search(DOMAIN_DSN, "(objectClass=person)", searchControls);
        assertEquals(1, Iterators.size(Iterators.forEnumeration(resultNamingEnumeration)));
    }

    @Test
    public void testContext() throws Exception {
        final Context context = embeddedLdapRule.context();
        final Object user = context.lookup("cn=Sondre Eikanger Kvalo,ou=people,dc=zapodot,dc=org");
        assertNotNull(user);
    }
}