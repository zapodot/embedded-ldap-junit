package org.zapodot.junit.ldap;

import com.google.common.collect.Iterators;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;
import org.junit.Rule;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
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
        final LDAPInterface ldapConnection = embeddedLdapRule.ldapConnection();
        final SearchResult searchResult = ldapConnection.search(DOMAIN_DSN, SearchScope.SUB, "(objectClass=person)");
        assertEquals(1, searchResult.getEntryCount());
    }

    @Test
    public void testDirContext() throws Exception {
        final DirContext dirContext = embeddedLdapRule.dirContext();
        final SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        final NamingEnumeration<javax.naming.directory.SearchResult> resultNamingEnumeration =
                dirContext.search(DOMAIN_DSN, "(objectClass=person)", searchControls);
        assertEquals(1, Iterators.size(Iterators.forEnumeration(resultNamingEnumeration)));
    }

    @Test
    public void testContext() throws Exception {
        final Context context = embeddedLdapRule.context();
        final Object user = context.lookup("cn=Sondre Eikanger Kvalo,ou=people,dc=zapodot,dc=org");
        assertNotNull(user);
    }

    @Test
    public void testContextClose() throws Exception {
        final Context context = embeddedLdapRule.context();
        context.close();
        assertNotNull(context.getNameInNamespace());

    }
}