package com.example.demo.controller;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.OCPPService;

@RestController
@RequestMapping("/ocpp")
public class OCPPController {

    private final OCPPService ocppService;

    public OCPPController(OCPPService ocppService) {
        this.ocppService = ocppService;
    }

    @PostMapping("/boot-notification")
    public ResponseEntity<String> bootNotification(@RequestBody String json) {
        JSONObject request = new JSONObject(json);
        ocppService.processBootNotification(request, null);
        return ResponseEntity.ok("BootNotification Processed");
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<String> heartbeat(@RequestBody String json) {
        JSONObject request = new JSONObject(json);
        ocppService.processHeartbeat(request, null);
        return ResponseEntity.ok("Heartbeat Processed");
    }

    @PostMapping("/status-notification")
    public ResponseEntity<String> statusNotification(@RequestBody String json) {
        JSONObject request = new JSONObject(json);
        ocppService.processStatusNotification(request, null);
        return ResponseEntity.ok("StatusNotification Processed");
    }

    @PostMapping("/start-transaction")
    public ResponseEntity<String> startTransaction(@RequestBody String json) {
        JSONObject request = new JSONObject(json);
        ocppService.processStartTransaction(request, null);
        return ResponseEntity.ok("StartTransaction Processed");
    }

    @PostMapping("/stop-transaction")
    public ResponseEntity<String> stopTransaction(@RequestBody String json) {
        JSONObject request = new JSONObject(json);
        ocppService.processStopTransaction(request, null);
        return ResponseEntity.ok("StopTransaction Processed");
    }
}
