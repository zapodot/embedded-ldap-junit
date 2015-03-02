package org.zapodot.junit.ldap.internal.jndi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;

public class ContextInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextInterceptor.class);


    public static void close() throws NamingException {
        LOGGER.debug("close() call intercepted. Will be ignored");
    }
}
