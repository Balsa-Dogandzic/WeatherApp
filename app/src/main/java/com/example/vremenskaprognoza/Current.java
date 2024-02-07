package com.example.vremenskaprognoza;

public class Current {
    /* Sadrzi podatke koje zelimo da prikazemo u detaljnom prikazu trenutnog vremena */
    String temperature;
    String windSpeed;
    String subjective;
    int is_day;
    String pressure;
    String condition;
    String participation;
    String icon;
    String humidity;

    public Current(String temperature, String windSpeed, String subjective, int is_day, String pressure, String condition, String participation, String icon, String humidity) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.subjective = subjective;
        this.is_day = is_day;
        this.pressure = pressure;
        this.condition = condition;
        this.participation = participation;
        this.icon = icon;
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature  + "°C";
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindSpeed() {
        return windSpeed + " km/h";
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getSubjective() {
        return subjective  + "°C";
    }

    public void setSubjective(String subjective) {
        this.subjective = subjective;
    }

    public int isDay() {
        return is_day;
    }

    public void setIs_day(int is_day) {
        this.is_day = is_day;
    }

    public String getPressure() {
        return pressure + " mb";
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getParticipation() {
        return participation + " mm";
    }

    public void setParticipation(String participation) {
        this.participation = participation;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHumidity() {
        return humidity + " g/kg";
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "Temperature: " + getTemperature() + '\n' +
                "Wind speed: " + getWindSpeed() + '\n' +
                "Subjective: " + getSubjective() + '\n' +
                "Pressure: " + getPressure() + '\n' +
                "Condition: " + getCondition() + '\n' +
                "Participation: " + getParticipation() + '\n' +
                "Humidity: " + getHumidity() + '\n';
    }
}
