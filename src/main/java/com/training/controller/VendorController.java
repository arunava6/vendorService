package com.training.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VendorController {

    @GetMapping("/")
    public String index() {

        return "Welcome to Spring Boot Vendor Service API!\n"
             + "=========================================\n"
             + "Status : RUNNING\n"
             + "Modules: Vendor Management System\n"
             + "API    : /vendor/controller/getVendors\n"
             + "=========================================";
    }
}