package com.experiment.chickenjohn.materialdemo;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

public class EcgData extends MeasuredData {
    private double recordTime = 0.000;
    private int dataId;
    private static double RECORDRATE = 1.0/500.0;

    //To insert current time automatically
    //the record time only changes when a second passed, why?
    public EcgData(int data, int dataId) {
        super("ECG", data);
        this.dataId = dataId;
        recordTime = (double) dataId * RECORDRATE;
    }

    public double getRecordTime() {
        return recordTime;
    }

    public int getDataId() {
        return dataId;
    }

    public static void setRecordRate (double herz){
        RECORDRATE = 1.0/herz;
    }

    public static double getRECORDRATE (){
        return RECORDRATE;
    }
}
