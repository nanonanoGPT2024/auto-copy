package com.scheduler.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scheduler.utils.CsvUtils;

@RestController
@RequestMapping("/api")
public class PhoneController {

    @Value("${app.folder.storage.file}")
    private String pathFile;

    @GetMapping("/readCsv")
    public ResponseEntity<Object> readCsv() {
        // List<String> data = CsvUtils.readCsvFile("file/Untitled.csv");
        List<String> data = CsvUtils.readCsvFile(pathFile);
        int total = data.size();

        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

}