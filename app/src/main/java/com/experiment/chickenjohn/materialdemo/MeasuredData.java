package com.experiment.chickenjohn.materialdemo;

public class MeasuredData {
    private String typeName;
    private int value;

    public MeasuredData(String typeName, int value) {
        super();
        this.typeName = typeName;
        this.value = value;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getValueInString() {
        return String.valueOf(value);
    }

    public int getValueInInt() {
        return value;
    }
}
