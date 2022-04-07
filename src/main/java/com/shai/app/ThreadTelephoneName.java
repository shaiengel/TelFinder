package com.shai.app;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class ThreadTelephoneName  extends Thread{

    String file;
    long lineCounter;
    public ThreadTelephoneName(String file){
        this.file = file;
    }

    public void run() {
        try {
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader("name","phone_number","source","num_of_records","location,work_email").withSkipHeaderRecord();
            CSVParser csvFileParser;


            try {
                csvFileParser = CSVParser.parse(new File(this.file), StandardCharsets.UTF_8, csvFileFormat);

                HashMap<String, ArrayList<Long>> listTelephoneName = Caching.getListTelephoneName();
                lineCounter = 1;
                for (CSVRecord record : csvFileParser) {
                    try {
                        lineCounter++;
                        String phone_number = getPhone(record);
                        insertToList(phone_number, lineCounter, listTelephoneName);

                    } catch (IllegalArgumentException e){
                        System.err.println("ThreadTelephoneName IllegalArgumentException lineCounter = " + lineCounter);
                        continue;
                    } catch (Exception e){
                        System.err.println("ThreadTelephoneName Exception lineCounter = " + lineCounter);
                        throw e;
                    }
                }
            } catch (Exception e) {
                System.err.println("ThreadTelephoneName Exception 1");
                throw e;
            }

        } catch (Exception e) {
            System.err.println("ThreadTelephoneName Exception 2");
        }

        Caching.setIsReadyTelephoneName(true);
        System.out.println("ThreadTelephoneName ready");
    }

    private String getPhone(CSVRecord record){
        try {
            String phone_number = record.get("phone_number");
            String[] arrOfStr = phone_number.split("-");
            return String.join("", arrOfStr);
        }catch (Exception e){
            throw e;
        }
    }

    private void insertToList(String phone_number, long line_number , HashMap<String, ArrayList<Long>> list){

        if (list.containsKey(phone_number)){
            ArrayList<Long> value = list.get(phone_number);
            value.add(line_number);
            list.replace(phone_number, value);
        }
        else{
            ArrayList<Long> value = new ArrayList<Long>();
            value.add(line_number);
            list.put(phone_number, value);
        }

        return;
    }
}
