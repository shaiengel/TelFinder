package com.shai.app;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HttpServer {

    @Autowired
    CsvRead csvRead;

    @GetMapping(path = "/caller_id")
    public ResponseEntity getTel(@RequestParam String phone_number){
        System.out.println("/GET caller_id?phone_number=" + phone_number);
        ResponseEntity.BodyBuilder builder;
        JSONObject jsonName = new JSONObject();
        try {


            String names = csvRead.readFile(phone_number);
            if (names == null) {
                builder = ResponseEntity.status(HttpStatus.NOT_FOUND);
                jsonName.put("ERROR", "No matching name for " + phone_number);

            } else {
                builder = ResponseEntity.status(HttpStatus.OK);
                jsonName.put("full_name", names.toString());
            }
        }catch (Exception e){
            System.err.println("/GET caller_id?phone_number=" + phone_number + " INTERNAL_SERVER_ERROR");
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            jsonName.put("ERROR", e.toString());
        }

        System.out.println("finished /GET caller_id?phone_number=" + phone_number);
        return builder.body(jsonName);

    }

    @PostMapping(path = "/remove")
    public ResponseEntity remove(@RequestParam String phone_number){
        System.out.println("/POST remove?phone_number=" + phone_number);
        ResponseEntity.BodyBuilder builder;
        JSONObject jsonName = new JSONObject();

        try {
            csvRead.removeRow(phone_number);
            builder = ResponseEntity.status(HttpStatus.ACCEPTED);
        }catch (Exception e){
            System.err.println("/POST remove?phone_number=" + phone_number + " INTERNAL_SERVER_ERROR");
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        System.out.println("finished /POST remove?phone_number=" + phone_number);
        return builder.body(jsonName);

    }
}