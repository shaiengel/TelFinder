package com.shai.app;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;

public class RemovalWorker implements Callable {

    long startPosition;
    long endPosition;
    String telephone;
    String filename;
    int threadNum;
    RandomAccessFile raf;
    long grace = 100;

    @Autowired
    ConfigurationReader config;

    public RemovalWorker(long startPosition, long endPosition, String telephone, String filename, int threadNum){
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.telephone = telephone;
        this.filename = filename;
        this.threadNum = threadNum;
    }

    @Override
    public Integer  call() {

        System.out.println("start RemovalWorker thread number = " + threadNum);
        long currentPos = 0;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(this.filename, "rw");

            raf.seek(startPosition);
            if (startPosition == 0){
                raf.readLine();
            }
            String line = "";
            String[] arrOfStr;
            String[] arrOfNum;
            String phone_number;
            long writePos;
            while (currentPos < endPosition + grace){

                writePos = raf.getFilePointer();
                line = raf.readLine();
                currentPos = raf.getFilePointer();
                try {
                    arrOfStr = line.split(",");
                    if (arrOfStr.length < 2){
                        continue;
                    }
                    arrOfNum = arrOfStr[1].split("-");
                    phone_number = String.join("", arrOfNum);

                    if(phone_number.equals(telephone)){
                        removeTelephoneFromLine(raf, writePos, line);
                    }
                }catch (NullPointerException e){
                    System.err.println("RemovalWorker NullPointerException thread number = " + threadNum);
                    return 0;
                }



            }


        } catch (Exception e) {
            System.err.println("RemovalWorker Exception thread number = " + threadNum);
        }

        System.out.println("finished RemovalWorker thread number = " + threadNum);
        return  0;

    }

    private void removeTelephoneFromLine(RandomAccessFile raf, long writePos, String line){
        try {
            long readPos = raf.getFilePointer();
            int a = line.indexOf(",");
            int b = line.indexOf(",", a + 1);

            byte[] buf = new byte[b - a - 1];
            for (int i = 0; i < buf.length; i++){
                buf[i] = ' ';
            }
            raf.seek(writePos + a + 1);
            raf.write(buf, 0, buf.length);
            raf.seek(readPos);

        }catch (Exception e){
            System.err.println("RemovalWorker removeTelephoneFromLine Exception");
        }
    }

}
