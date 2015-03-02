package org.zapodot.junit.ldap.internal.jndi;

import javax.naming.Context;

public interface ContextProxy {

    void setDelegatedContext(final Context context);
}
