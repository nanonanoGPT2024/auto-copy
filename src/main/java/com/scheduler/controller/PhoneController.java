package com.scheduler.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scheduler.utils.CsvUtils;

@RestController
@RequestMapping("/api")
public class PhoneController {

    @GetMapping("/readCsv")
    public List<String> readCsv() {
        return CsvUtils.readCsvFile("file/Untitled.csv");
    }
}