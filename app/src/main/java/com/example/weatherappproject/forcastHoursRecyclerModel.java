package com.example.weatherappproject;

public class forcastHoursRecyclerModel {
    String forcastHour, forcastIcon, forcastCondition;
    int forcastHourTemp, forcastWind;

    public forcastHoursRecyclerModel(String forcastHour, String forcastIcon, String forcastCondition, int forcastHourTemp, int forcastWind) {
        this.forcastHour = forcastHour;
        this.forcastIcon = forcastIcon;
        this.forcastCondition = forcastCondition;
        this.forcastHourTemp = forcastHourTemp;
        this.forcastWind = forcastWind;
    }
}
