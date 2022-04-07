package com.shai.app;

import java.io.RandomAccessFile;
import java.util.HashMap;

public class ThreadBufferSize extends Thread{

    String file;
    long lineCounter;

    public ThreadBufferSize(String file){
       this.file = file;
    }

    public void run() {

        try {
            RandomAccessFile raf = new RandomAccessFile(this.file, "rw");
            //raf.readLine();
            lineCounter = 1;

            HashMap<Long, Long> list = Caching.getListBufferSize();


            while (raf.readLine() != null){
                lineCounter++;
                list.put(lineCounter, raf.getFilePointer());
            }

        } catch (Exception e) {
            System.err.println("ThreadBufferSize error lineCounter = " + lineCounter);
        }

        Caching.setIsReadylistBufferSize(true);
        System.out.println("ThreadBufferSize ready");
    }
}
