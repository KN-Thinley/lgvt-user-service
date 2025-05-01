package com.lgvt.user_service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MockitostubTest {
    @Test
    void findTheGreatestFromAllData() {
        Mockito mockito = new Mockito(new DataServiceStub());
        int result = mockito.findTheGreaestFromAllData();
        assertEquals(3, result);
    }
}

class DataServiceStub implements DataService {
    @Override
    public int[] retrieveAllData() {
        return new int[] { 1, 2, 3 };
    }
}
