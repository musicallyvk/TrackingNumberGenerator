package com.example.TrackingNumber_Service.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Service
public class TrackingNumberGenerator {

    private static final long MAX_RANGE = 1000000000000L;
    private static final String startsWith = "UNQ";
    private String endsWith;
    private static final SecureRandom random = new SecureRandom();

    Logger logger = LoggerFactory.getLogger(TrackingNumberGenerator.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public TrackingNumberGenerator() {
        this.endsWith = "-T_ID";
    }


    public String generateUniqueTrackingNumber() {
        try {
            long randomLong = generatingRandomLong();
            String randomNumber = String.format("%010d", randomLong);
            return startsWith + randomNumber + endsWith;
        } catch (Exception ex) {
            logger.error("Failed to generate unique tracking number: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to generate unique tracking number");
        }
    }

    public Set<String> generateBulkUniqueTrackingNumbers(int givenSize) {
        try {
            if (givenSize <= 0) {
                throw new IllegalArgumentException("Required size must be greater than zero");
            }
            HashSet<String> uniqueTrackingNumbers = new HashSet<>();
            logger.info("Generating Some bulk tracking numbers with size: {}", givenSize);
            while (uniqueTrackingNumbers.size() < givenSize) {
                String uniqueTrackingNumber = generateUniqueTrackingNumber();
                if (isTrackingNumberUnique(uniqueTrackingNumber)) {
                    uniqueTrackingNumbers.add(uniqueTrackingNumber);
                }
            }
            return uniqueTrackingNumbers;
        } catch (Exception ex) {
            logger.error("Failed to generate bulk unique tracking numbers: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to generate bulk unique tracking numbers");
        }
    }


    private boolean isTrackingNumberUnique(String trackingNumber) {
        String lockKey = "trackingNumberLock";
        String lockValue = Long.toString(System.currentTimeMillis());

        boolean locked = false;
        try {
            locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue);
            if (locked) {
                // Check if the tracking number already exists in Redis (simulating a database lookup)
                if (redisTemplate.opsForValue().get(trackingNumber) == null) {
                    // If not exists, store the tracking number in Redis (simulating storing in database)
                    redisTemplate.opsForValue().set(trackingNumber, "generated");
                    return true;
                }
            }
        } finally {
            // Release the lock
            if (locked) {
                redisTemplate.delete(lockKey);
            }
        }
        return false;
    }


    private long generatingRandomLong() {
        long randomLong = random.nextLong();
        if (randomLong < 0) {
            randomLong = -randomLong;
        }
        return randomLong % MAX_RANGE;
    }


}
