package com.experimental.workshops.willtmwu.nfc_home3c.nfc_spec.model;

public class BaseRecord {

    public  int MB; // Message Begin
    public  int ME; // Message End
    public  int SR; // Short Record
    public  int tnf; //3-bit TNF (Type Name Format)
    public  byte[] type;

    public String payload;

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("MB:" + MB);
        buffer.append(" ME:" + ME);
        buffer.append(" SR:" + SR);
        buffer.append(" TNF:" + tnf);

        return buffer.toString();
    }

    static int[] getHeader(byte[] payload) {
        byte header = payload[0];
        int[] result = new int[3];

        // Mask MB
        result[0] = (header & 0x80) >> 7;
        result[1] = (header & 0x40) >> 6;
        result[2] = (header & 0x10) >> 4;

        return result;
    }
}