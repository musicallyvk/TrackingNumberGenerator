package com.example.TrackingNumber_Service.Controller;

import com.example.TrackingNumber_Service.Service.TrackingNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/tracking")
public class TrackingNumberController {

    @Autowired
    private TrackingNumberGenerator trackingNumberGenerator;

    @GetMapping("/generate")
    public String generateTrackingNumber() {

        return trackingNumberGenerator.generateUniqueTrackingNumber();
    }

    @GetMapping("/generateBulk")
    public Set<String> generateBulkTrackingNumbers(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }
        return trackingNumberGenerator.generateBulkUniqueTrackingNumbers(size);
    }

}
