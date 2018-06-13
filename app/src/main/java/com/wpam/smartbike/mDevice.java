package com.wpam.smartbike;

class mDevice {
    String DeviceName;
    String MacAddress;
    String PairedStatus;

    public mDevice(String nDeviceName, String nMacAddress, String nPairedStatus) {
        DeviceName = nDeviceName;
        MacAddress = nMacAddress;
        PairedStatus = nPairedStatus;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public String getPairedStatus() {
        return PairedStatus;
    }
}
