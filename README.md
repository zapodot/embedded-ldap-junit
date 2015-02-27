# embedded-ldap-junit
A JUnit rule for running an embedded LDAP server in your JUnit test based on the wonderful [UnboundID LDAP SDK](https://www.ldap.com/unboundid-ldap-sdk-for-java).

## Why?
* you want to test your LDAP integration code without affecting your LDAP server
* you are working with LDAP schema changes that you would like to test without changing the actual LDAP server
* you are refactoring legacy code where LDAP calls is tightly coupled with your business logic and wants to start by testing the legacy code from the "outside" (as suggested by [Michael Feathers](http://www.informit.com/store/working-effectively-with-legacy-code-9780131177055?aid=15d186bd-1678-45e9-8ad3-fe53713e811b))

## Status
This library is distributed through the [Sonatype OSS repo](https://oss.sonatype.org/) and should thus be widely available.
Java 7 or higher is required.

## Changelog
* version 0.1: first release

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
@Rule
public EmbeddedLdapRule embeddedLdapRule = EmbeddedLdapRuleBuilder
        .newInstance()
        .usingDomainDsn(DOMAIN_DSN)
        .importingLdifs("example.ldif")
        .build();

@Test
public void testLdapConnection() throws Exception {
    // Test using the UnboundID LDAP SDK directly
    final LDAPConnection ldapConnection = embeddedLdapRule.ldapConnection();
    final SearchResult searchResult = ldapConnection.search(DOMAIN_DSN, SearchScope.SUB, "(objectClass=person)");
    assertEquals(1, searchResult.getEntryCount());
}

@Test
public void testInitialDirContext() throws Exception {

    // Test using the good ol' JDNI-LDAP integration
    final InitialDirContext initialDirContext = embeddedLdapRule.initialDirContext();
    final SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    final NamingEnumeration<javax.naming.directory.SearchResult> resultNamingEnumeration =
            initialDirContext.search(DOMAIN_DSN, "(objectClass=person)", searchControls);
    assertEquals(1, Iterators.size(Iterators.forEnumeration(resultNamingEnumeration)));
}
```

