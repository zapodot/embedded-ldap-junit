# embedded-ldap-junit [![Build Status](https://travis-ci.org/zapodot/jackson-databind-java-optional.svg?branch=master)](https://travis-ci.org/zapodot/jackson-databind-java-optional) [![Coverage Status](https://coveralls.io/repos/zapodot/embedded-ldap-junit/badge.svg)](https://coveralls.io/r/zapodot/embedded-ldap-junit)

A [JUnit Rule](//github.com/junit-team/junit/wiki/Rules) for running an embedded LDAP server in your JUnit test based on the wonderful [UnboundID LDAP SDK](https://www.ldap.com/unboundid-ldap-sdk-for-java).

## Why?
* you want to test your LDAP integration code without affecting your LDAP server
* you are working with LDAP schema changes that you would like to test without changing the schema at the shared LDAP server
* you are refactoring legacy code where LDAP calls is tightly coupled with your business logic and wants to start by testing the legacy code from the "outside" (as suggested by [Michael Feathers](http://www.informit.com/store/working-effectively-with-legacy-code-9780131177055?aid=15d186bd-1678-45e9-8ad3-fe53713e811b))
    * for this exact reason all instances returned from the EmbeddedLdapRule is instrumented to suppress "close" calls so that your legacy code will not destroy the current Context or LdapInterface

## Status
This library is distributed through the [Sonatype OSS repo](https://oss.sonatype.org/) and should thus be widely available.
Java 7 or higher is required. It is currently at an early stage of development so breaking changes may occur :-)

## Changelog
* version 0.1 (in progress): first release

## Usage

### Add dependency
#### Maven
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-ldap-junit</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

#### SBT
```scala
libraryDependencies += "org.zapodot" % "embedded-ldap-junit" % "0.1-SNAPSHOT"
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
        .usingDomainDsn(DOMAIN_DSN)
        .importingLdifs("example.ldif")
        .build();

@Test
public void testLdapConnection() throws Exception {
    // Test using the UnboundID LDAP SDK directly
    final LdapInterface ldapConnection = embeddedLdapRule.ldapConnection();
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

