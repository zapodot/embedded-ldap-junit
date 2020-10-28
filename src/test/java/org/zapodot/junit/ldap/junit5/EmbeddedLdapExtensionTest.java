package org.zapodot.junit.ldap.junit5;

import com.google.common.collect.Iterators;
import com.unboundid.ldap.sdk.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddedLdapExtensionTest {

    public static final String DOMAIN_DSN = "dc=zapodot,dc=org";

    @RegisterExtension
    public EmbeddedLdapExtension embeddedLdapExtension = EmbeddedLdapExtensionBuilder
            .newInstance()
            .usingDomainDsn(DOMAIN_DSN)
            .importingLdifs("example.ldif")
            .build();

    @Test
    void testLdapConnection() throws Exception {
        final LDAPInterface ldapConnection = embeddedLdapExtension.ldapConnection();
        final SearchResult searchResult = ldapConnection.search(DOMAIN_DSN, SearchScope.SUB, "(objectClass=person)");
        assertEquals(1, searchResult.getEntryCount());
    }

    @Test
    void testRawLdapConnection() throws Exception {
        final String commonName = "Test person";
        final String dn = String.format(
                "cn=%s,ou=people,dc=zapodot,dc=org",
                commonName);
        LDAPConnection ldapConnection = embeddedLdapExtension.unsharedLdapConnection();
        try {
            ldapConnection.add(new AddRequest(dn, Arrays.asList(
                    new Attribute("objectclass", "top", "person", "organizationalPerson", "inetOrgPerson"),
                    new Attribute("cn", commonName), new Attribute("sn", "Person"), new Attribute("uid", "test"))));
        } finally {
            // Forces the LDAP connection to be closed. This is not necessary as the rule will usually close it for you.
            ldapConnection.close();
        }
        ldapConnection = embeddedLdapExtension.unsharedLdapConnection();
        final SearchResultEntry entry = ldapConnection.searchForEntry(new SearchRequest(dn,
                                                                                        SearchScope.BASE,
                                                                                        "(objectClass=person)"));
        assertNotNull(entry);
    }

    @Test
    void testDirContext() throws Exception {
        final DirContext dirContext = embeddedLdapExtension.dirContext();
        final SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        final NamingEnumeration<javax.naming.directory.SearchResult> resultNamingEnumeration =
                dirContext.search(DOMAIN_DSN, "(objectClass=person)", searchControls);
        assertEquals(1, Iterators.size(Iterators.forEnumeration(resultNamingEnumeration)));
    }

    @Test
    void testContext() throws Exception {
        final Context context = embeddedLdapExtension.context();
        final Object user = context.lookup("cn=Sondre Eikanger Kvalo,ou=people,dc=zapodot,dc=org");
        assertNotNull(user);
    }

    @Test
    void testContextClose() throws Exception {
        final Context context = embeddedLdapExtension.context();
        context.close();
        assertNotNull(context.getNameInNamespace());

    }

    @Test
    void testEmbeddedServerPort() {
        assertTrue(embeddedLdapExtension.embeddedServerPort() > 0);

    }

    @Test
    void testNoPortAssignedYet() {
        final EmbeddedLdapExtension embeddedLdapRule = new EmbeddedLdapExtensionBuilder().build();

        assertThrows(IllegalStateException.class, embeddedLdapRule::embeddedServerPort);

    }
}
