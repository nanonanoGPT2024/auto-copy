package com.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scheduler.service.AutoRecordingService;

@RestController
@RequestMapping("AutoMoveRecording")
public class AutoRecordingController {

    @Autowired
    private AutoRecordingService service;

    @GetMapping()
    public String AutoMoving() {

        String message =  service.importData();

        return "Successfully : " + message;
    }
}
