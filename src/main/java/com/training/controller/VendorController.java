package com.training.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.training.business.bean.VendorBean;
import com.training.service.VendorService;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class VendorController {

	private static final Logger logger = LoggerFactory.getLogger(VendorController.class);
	
	/*
	 * Autowire the VendorService object
	 * 
	 * */
	@Autowired
	private VendorService vendorService;
	
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "Welcome to Spring Boot Vendor Service API!";
	}

	
	/* 
	 * Method - getVendorDetails()
	 * Fetch all the vendor details using VendorService and store it inside a List
	 * Return a ResponseEntity object passing the list of vendor details
	 * 
	 */
	
	@RequestMapping(value="/vendor/controller/getVendors", method=RequestMethod.GET)
	public ResponseEntity<List<VendorBean>> getVendorDetails() {
		List<VendorBean> vendors = vendorService.getVendorDetails();
		return ResponseEntity.ok(vendors);
	}
}
