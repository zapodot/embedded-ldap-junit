package org.zapodot.junit.ldap.internal.jndi;

import javax.naming.directory.DirContext;

public interface DirContextProxy {

    void setDelegatedDirContext(final DirContext dirContext);
}
