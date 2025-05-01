package com.lgvt.user_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class MathTest {
    @Test
    void test() {
        Math math = new Math();
        int[] numbers = { 1, 2, 3, 4, 5 };
        int expectedSum = 15;
        int actualSum = math.calculateSum(numbers);

        assertEquals(expectedSum, actualSum, "The sum should be 15");
    }
}
