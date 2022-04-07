package com.shai.app;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

public class Caching{

    static HashMap<Long, Long> listBufferSize = new HashMap<Long, Long>();
    static HashMap<String, ArrayList<Long>> listTelephoneName = new HashMap<String, ArrayList<Long>>();

    static boolean isReadylistBufferSize = false;
    static boolean isReadyTelephoneName = false;

    public static HashMap<Long, Long> getListBufferSize() {
        return listBufferSize;
    }

    public static HashMap<String, ArrayList<Long>> getListTelephoneName() {
        return listTelephoneName;
    }

    public static boolean isIsReadylistBufferSize() {
        return isReadylistBufferSize;
    }

    public static void setIsReadylistBufferSize(boolean isReadylistBufferSize) {
        Caching.isReadylistBufferSize = isReadylistBufferSize;
    }

    public static boolean isIsReadyTelephoneName() {
        return isReadyTelephoneName;
    }

    public static void setIsReadyTelephoneName(boolean isReadyTelephoneName) {
        Caching.isReadyTelephoneName = isReadyTelephoneName;
    }









}
