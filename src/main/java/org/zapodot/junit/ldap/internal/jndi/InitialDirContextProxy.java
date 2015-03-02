package org.zapodot.junit.ldap.internal.jndi;

import javax.naming.directory.InitialDirContext;

public interface InitialDirContextProxy {

    InitialDirContext getDelegatedInitialDirContext();

    void setDelegatedInitialDirContext(final InitialDirContext initialContext);
}
