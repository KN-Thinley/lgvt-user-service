package com.lgvt.user_service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BeforeAfterTest {
    @BeforeAll
    static void beforeAll() {
        System.out.println("Before All Tests");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before Each Test");
    }

    @Test
    void test() {
        System.out.println("Test");
    }

    @Test
    void test1() {
        System.out.println("Test 1");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After Each Test");
    }

    @AfterAll
    static void AfterAll() {
        System.out.println("After Each Test");
    }
}
