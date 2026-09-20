package com.example.parking;

import com.example.parking.config.ParkingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties(ParkingProperties.class)
@EnableTransactionManagement
public class ParkingManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingManagementApplication.class, args);
    }
}
