package com.example.botany;

public class PlantDataHelperClass {

    String plantName,plantType,plantId,plantHeight;


    public PlantDataHelperClass() {
    }

    public PlantDataHelperClass(String plantName, String plantType, String plantId, String plantHeight) {
        this.plantName = plantName;
        this.plantType = plantType;
        this.plantId =  plantId;
        this.plantHeight = plantHeight;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getPlantHeight() {
        return plantHeight;
    }

    public void setPlantHeight(String plantHeight) {
        this.plantHeight = plantHeight;
    }
}
