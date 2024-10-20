package org.cardvault.tests.user;

import org.cardvault.core.logging.Logger;
import org.cardvault.testRunner.annotations.Test;
import org.cardvault.testRunner.annotations.TestClass;


@TestClass
public class UserTest {
    @Test
    public void testRegister() {
        Logger.debug("Simulating user registration test...");
        throw new RuntimeException("Test failed");
    }

    @Test
    public void testLogin() {
        Logger.debug("Simulating user login test...");
    }

    @Test
    public void testLogout() {
        Logger.debug("Simulating user logout test...");
    }
}
