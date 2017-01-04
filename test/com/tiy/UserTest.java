package com.tiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Paul Dennis on 1/4/2017.
 */
public class UserTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testOneParamConstructor () {
        User user = new User (0, "tester@test");
        assertEquals("tester", user.getFullName());
    }

    @Test
    public void testOneParamConstructorNoAt  () {
        User user = new User(0, "tester paul");
        assertEquals("tester paul", user.getFullName());
    }
}