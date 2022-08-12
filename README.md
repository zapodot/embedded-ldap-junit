# embedded-ldap-junit
[![Build Status](https://github.com/zapodot/embedded-ldap-junit/actions/workflows/maven.yml/badge.svg)](https://github.com/zapodot/embedded-ldap-junit/actions/workflows/maven.yml) [![codecov](https://codecov.io/gh/zapodot/embedded-ldap-junit/branch/master/graph/badge.svg?token=2jm8uT1bJg)](https://codecov.io/gh/zapodot/embedded-ldap-junit) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.zapodot/embedded-ldap-junit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.zapodot/embedded-ldap-junit) [![Libraries.io for GitHub](https://img.shields.io/librariesio/github/zapodot/embedded-ldap-junit.svg)](https://libraries.io/github/zapodot/embedded-ldap-junit) [![GitHub](https://img.shields.io/github/license/zapodot/embedded-ldap-junit)](https://github.com/zapodot/embedded-ldap-junit/blob/master/LICENSE) [![Analytics](https://ga-beacon.appspot.com/UA-40926073-2/embedded-ldap-junit/README.md)](https://github.com/igrigorik/ga-beacon)

A [JUnit Rule](//github.com/junit-team/junit/wiki/Rules) for running an embedded LDAP server in your JUnit test based on the wonderful [UnboundID LDAP SDK](https://www.ldap.com/unboundid-ldap-sdk-for-java). Inspired by the [Embedded Database JUnit Rule](//github.com/zapodot/embedded-db-junit).

## Why?
* you want to test your LDAP integration code without affecting your LDAP server
* you are working with LDAP schema changes that you would like to test without changing the schema at the shared LDAP server
* you are refactoring legacy code where LDAP calls are tightly coupled with your business logic and wants to start by testing the legacy code from the "outside" (as suggested by [Michael Feathers](http://www.informit.com/store/working-effectively-with-legacy-code-9780131177055?aid=15d186bd-1678-45e9-8ad3-fe53713e811b))
    * for this exact reason most instances returned from the EmbeddedLdapRule is instrumented to suppress "close" calls so that your legacy code will not destroy the current Context/DirContext or LdapInterface
    * **note**: if you used the new unsharedLdapConnection() method, the returned instance will not have this guarantee as it returns a instance of UnboundID LDAPConnection which is declared 'final'.

## Status
This library is distributed through the [Sonatype OSS repo](https://oss.sonatype.org/) and should thus be widely available.
Java 8 or higher is required. It has proven pretty useful for several users and should be considered safe for running tests for all kinds of LDAP integrating code.

## Changelog
See [releases](//github.com/zapodot/embedded-ldap-junit/releases)

## Usage

### Add dependency
#### Maven
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-ldap-junit</artifactId>
    <version>0.9.0</version>
</dependency>
```

### Add to Junit test
```java
import com.unboundid.ldap.sdk.LDAPInterface;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
...

@Rule
public EmbeddedLdapRule embeddedLdapRule = EmbeddedLdapRuleBuilder
        .newInstance()
        .usingDomainDsn("dc=example,dc=com")
        .importingLdifs("example.ldif")
        .build();

@Test
public void testLdapInteface() throws Exception {
    // Test using the UnboundID LDAP SDK directly
    final LdapInterface ldapConnection = embeddedLdapRule.ldapConnection();
    final SearchResult searchResult = ldapConnection.search(DOMAIN_DSN, SearchScope.SUB, "(objectClass=person)");
    assertEquals(1, searchResult.getEntryCount());
}

@Test
public void testUnsharedLdapConnection() throws Exception {
    // Test using the UnboundID LDAP SDK directly by using the UnboundID LDAPConnection type
    final LDAPConnection ldapConnection = embeddedLdapRule.unsharedLdapConnection();
    final SearchResult searchResult = ldapConnection.search(DOMAIN_DSN, SearchScope.SUB, "(objectClass=person)");
    assertEquals(1, searchResult.getEntryCount());
}

@Test
public void testDirContext() throws Exception {

    // Test using the good ol' JDNI-LDAP integration
    final DirContext dirContext = embeddedLdapRule.dirContext();
    final SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    final NamingEnumeration<javax.naming.directory.SearchResult> resultNamingEnumeration =
            dirContext.search(DOMAIN_DSN, "(objectClass=person)", searchControls);
    assertEquals(1, Iterators.size(Iterators.forEnumeration(resultNamingEnumeration)));
}
@Test
public void testContext() throws Exception {

    // Another test using the good ol' JDNI-LDAP integration, this time with the Context interface
    final Context context = embeddedLdapRule.context();
    final Object user = context.lookup("cn=John Doe,ou=people,dc=example,dc=com");
    assertNotNull(user);
}
```
