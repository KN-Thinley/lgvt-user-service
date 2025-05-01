package com.lgvt.user_service;

public class Mockito {
    private DataService dataService;

    public Mockito(DataService dataService) {
        super();
        this.dataService = dataService;
    }

    public int findTheGreaestFromAllData() {
        int[] data = dataService.retrieveAllData();
        int greatest = data[0];
        for (int value : data) {
            if (value > greatest) {
                greatest = value;
            }
        }
        return greatest;
    }

}

interface DataService {
    int[] retrieveAllData();
}