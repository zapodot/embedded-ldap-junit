package org.zapodot.junit.ldap;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.Rule;
import org.junit.Test;

import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;

public class EmbeddedLdapRuleStarttlsTest {

    public static final String DOMAIN_DSN = "dc=zapodot,dc=org";

    final SSLContext sslContext;
    {
        try {
            sslContext = buildSslContext();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create LDAPS config", e);
        }
    }

    @Rule
    public EmbeddedLdapRule embeddedLdapRule = EmbeddedLdapRuleBuilder
        .newInstance()
        .usingDomainDsn(DOMAIN_DSN)
        .importingLdifs("example.ldif")
        .withListener(getListenerConfig())
        .build();

    private InMemoryListenerConfig getListenerConfig() {
        try {
            return InMemoryListenerConfig.createLDAPConfig(
                "tls", InetAddress.getLoopbackAddress(), 0, sslContext.getSocketFactory()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create LDAPS config", e);
        }
    }

    @Test
    public void testRawLdapConnection() throws Exception {
        final String commonName = "Test person";
        final String dn = String.format(
            "cn=%s,ou=people,dc=zapodot,dc=org",
            commonName);
        LDAPConnection ldapConnection = embeddedLdapRule.unsharedLdapConnection();
        ldapConnection.processExtendedOperation(new StartTLSExtendedRequest(sslContext));
        try {
            ldapConnection.add(new AddRequest(dn, Arrays.asList(
                new Attribute("objectclass", "top", "person", "organizationalPerson", "inetOrgPerson"),
                new Attribute("cn", commonName), new Attribute("sn", "Person"), new Attribute("uid", "test"))));
        } finally {
            // Forces the LDAP connection to be closed. This is not necessary as the rule will usually close it for you.
            ldapConnection.close();
        }
        ldapConnection = embeddedLdapRule.unsharedLdapConnection();
        final SearchResultEntry entry = ldapConnection.searchForEntry(new SearchRequest(dn,
            SearchScope.BASE,
            "(objectClass=person)"));
        assertNotNull(entry);
    }

    public static SSLContext buildSslContext()
        throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
        UnrecoverableKeyException, KeyManagementException, OperatorCreationException {
        KeyStore keystore = KeyStore.getInstance("jks");
        keystore.load(null, new char[] {});
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2014);
        final KeyPair keyPair = gen.generateKeyPair();

        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        org.bouncycastle.asn1.x500.X500Name dn = new org.bouncycastle.asn1.x500.X500Name("cn=localhost");
        BigInteger sn = new BigInteger(Long.toString(now));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.HOUR, 1);
        Date endDate = calendar.getTime();

        String signatureAlgorithm = "SHA256WithRSA";

        final ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());

        final X509CertificateHolder holder =
            new JcaX509v3CertificateBuilder(dn, sn, startDate, endDate, dn, keyPair.getPublic())
                .addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(true))
                .build(contentSigner);
        final X509Certificate cert = new JcaX509CertificateConverter()
            .setProvider(bcProvider)
            .getCertificate(holder);
        keystore.setCertificateEntry("test", cert);
        keystore.setKeyEntry("key", keyPair.getPrivate(), new char[] {}, new Certificate[] { cert });

        final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, new char[] {});
        final KeyManager[] kms = kmfactory.getKeyManagers();

        KeyStore truststore = KeyStore.getInstance("jks");
        truststore.load(null, new char[] {});
        truststore.setCertificateEntry("test", cert);

        final TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        );
        tmfactory.init(truststore);
        final TrustManager[] tms = tmfactory.getTrustManagers();
        final SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(kms, tms, null);
        return sslcontext;
    }
}