package com.shai.app;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

@Component
public class CsvRead {

    @Autowired
    ConfigurationReader config;


    public String readFileOptimized(String telephone) throws IOException {

        RandomAccessFile raf = new RandomAccessFile(config.getCsvFile(), "rw");

        ArrayList<Long> list = Caching.getListTelephoneName().get(telephone);
        if (list == null){
            return null;
        }
        HashMap<Long, Long> listBufferSize = Caching.getListBufferSize();
        LinkedHashSet<String> names=new LinkedHashSet();

        for (Long lineNumber : list){
            long pos = listBufferSize.get(lineNumber);
            raf.seek(pos);
            String line = raf.readLine();

            String[] arrOfStr = line.split(",");
            if (arrOfStr.length == 0){
                continue;
            }
            insertToList(arrOfStr[0], names);

        }
        return names.toString();

    }
    public String readFile(String telephone) throws IOException {

        if (Caching.isIsReadylistBufferSize() && Caching.isIsReadyTelephoneName()){
            return readFileOptimized(telephone);
        }

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader("name","phone_number","source","num_of_records","location,work_email").withSkipHeaderRecord();
        CSVParser csvFileParser;
        LinkedHashSet<String> names=new LinkedHashSet();

        try {
            csvFileParser = CSVParser.parse(new File(config.getCsvFile()), StandardCharsets.UTF_8, csvFileFormat);

            for (CSVRecord record : csvFileParser) {
                try {
                    String phone_number = getPhone(record);
                    if (telephone.equals(phone_number)) {
                        String name = record.get("name");
                        insertToList(name, names);
                    }
                } catch (IllegalArgumentException e){
                    System.err.println("readFile IllegalArgumentException");
                    continue;
                } catch (Exception e){
                    System.err.println("readFile Exception 1");
                    throw e;
                }
            }
        } catch (Exception e) {
            System.err.println("readFile Exception 2");
            throw e;
        }
        return names.toString();
    }

    public void removeRowOptimized(String telephone){

        try {
            RandomAccessFile raf = new RandomAccessFile(config.getCsvFile(), "rw");

            HashMap<String, ArrayList<Long>> listTelephoneName = Caching.getListTelephoneName();
            ArrayList<Long> list = listTelephoneName.get(telephone);
            if (list == null) {
                return;
            }
            HashMap<Long, Long> listBufferSize = Caching.getListBufferSize();

            for (Long lineNumber : list) {
                Long pos = listBufferSize.get(lineNumber);
                raf.seek(pos);
                removeTelephoneFromLine(raf);
            }
            listTelephoneName.remove(telephone);
        } catch (Exception e) {
            System.err.println("removeRowOptimized Exception");
        }
        return;


    }

    public void removeRow(String telephone){

        if (Caching.isIsReadylistBufferSize() && Caching.isIsReadyTelephoneName()){
            removeRowOptimized(telephone);
            return;
        }

        long fileSize;
        int poolSize = config.getPoolSize();
        long chunkSize;
        Path path = Paths.get(config.getCsvFile());
        try {

            fileSize = Files.size(path);
            chunkSize = fileSize / poolSize;

            ExecutorService service = Executors.newFixedThreadPool(poolSize);
            List<Future<Callable>> futures = new ArrayList<Future<Callable>>();

            long startPosition = 0;
            long endPosition;
            for (int n = 0; n < poolSize; n++)
            {
                endPosition = startPosition + chunkSize;
                Future f = service.submit(new RemovalWorker(startPosition, endPosition, telephone, config.getCsvFile(), n));
                futures.add(f);
                startPosition = endPosition;
            }

            // wait for all tasks to complete before continuing
            for (Future<Callable> f : futures){
                f.get();
            }

            //shut down the executor service so that this thread can exit
            service.shutdownNow();

        } catch (IOException e) {
            System.err.println("removeRow IOException");

        } catch (ExecutionException e) {
            System.err.println("removeRow ExecutionException");

        } catch (InterruptedException e) {
            System.err.println("removeRow InterruptedException");

        }
        return;

    }

 /*   public boolean removeRowOld(String telephone){

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader("name","phone_number","source","num_of_records","location,work_email");
        CSVParser csvFileParser;
        Integer lineNumber = 0;
        String line;
        String phone_number;
        List<Integer> lineNumbers = new ArrayList<>();

        try {
            csvFileParser = CSVParser.parse(new File(config.getCsvFile()), StandardCharsets.UTF_8, csvFileFormat);

            for (CSVRecord record : csvFileParser) {
                try {
                    phone_number = getPhone(record);
                    lineNumber++;
                }catch (IllegalArgumentException e){
                    continue;
                } catch (Exception e){
                    throw e;
                }
                if (telephone.equals(phone_number)) {
                    lineNumbers.add(lineNumber);
                }
            }
        } catch (Exception e) {
            return false;
        }


        try {
            RandomAccessFile raf = new RandomAccessFile(config.getCsvFile(), "rw");
            Integer lastLine = lineNumbers.get(lineNumbers.size()-1);
            Iterator<Integer> iter = lineNumbers.iterator();

            raf.readLine();
            int nextLine = iter.next();
            for (int i = 2; i <= lastLine; i++){
                if (i == nextLine)
                {
                    removeTelephoneFromLine(raf);
                    if (iter.hasNext()) {
                        nextLine = iter.next();
                    }
                }
                raf.readLine();
            }
            raf.close();
        } catch (Exception e) {
            return false;
        }
        return true;

    }*/

    private void removeTelephoneFromLine(RandomAccessFile raf){
        try {
            long writePos = raf.getFilePointer();
            String line = raf.readLine();
            int a = line.indexOf(",");
            int b = line.indexOf(",", a + 1);
            //long readPos = raf.getFilePointer();

            byte[] buf = new byte[b - a - 1];
            for (int i = 0; i < buf.length; i++){
                buf[i] = ' ';
            }
            raf.seek(writePos + a + 1);
            raf.write(buf, 0, buf.length);
            raf.seek(writePos);

        }catch (Exception e){

        }
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

    private void insertToList(String name, LinkedHashSet<String> names){
        String name1 = name.replaceAll("[^a-zA-Z0-9 ]", "");
        String[] arrOfStr = name1.split(" ");
        if (arrOfStr.length >= 2){
            name1 = arrOfStr[arrOfStr.length-2] + " " + arrOfStr[arrOfStr.length-1];
        }
        else if (arrOfStr.length == 1){
            name1 = arrOfStr[arrOfStr.length-1];
        }
        else{
            return;
        }
        names.add(name1);
        return;
    }


}
