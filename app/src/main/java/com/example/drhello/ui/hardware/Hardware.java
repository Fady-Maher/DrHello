package com.example.drhello.ui.hardware;

public class Hardware {
    private Double Heart_Rate,SPO2,Temperature_C,Temperature_F;

    public Hardware() {
    }

    public Hardware(Double heart_Rate, Double SPO2, Double temperature_C, Double temperature_F) {
        Heart_Rate = heart_Rate;
        this.SPO2 = SPO2;
        Temperature_C = temperature_C;
        Temperature_F = temperature_F;
    }

    public Double getHeart_Rate() {
        return Heart_Rate;
    }

    public void setHeart_Rate(Double heart_Rate) {
        Heart_Rate = heart_Rate;
    }

    public Double getSPO2() {
        return SPO2;
    }

    public void setSPO2(Double SPO2) {
        this.SPO2 = SPO2;
    }

    public Double getTemperature_C() {
        return Temperature_C;
    }

    public void setTemperature_C(Double temperature_C) {
        Temperature_C = temperature_C;
    }

    public Double getTemperature_F() {
        return Temperature_F;
    }

    public void setTemperature_F(Double temperature_F) {
        Temperature_F = temperature_F;
    }
}
