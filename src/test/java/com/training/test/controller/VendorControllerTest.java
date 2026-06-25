package com.training.test.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.training.controller.VendorController;
import com.training.service.VendorService;

@WebMvcTest(VendorController.class)
public class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorService vendorServiceMock;

    @Test
    public void indexVendorControllerTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
               .andExpect(status().isOk())
               .andExpect(content().string("Welcome to Spring Boot Vendor Service API!"));
    }
    
    @Test
    public void getVendorDetailsTest() throws Exception {
    	when(vendorServiceMock.getVendorDetails())
    	.thenReturn(Collections.emptyList());
    	
    	mockMvc.perform(
    			MockMvcRequestBuilders.get("/vendor/controller/getVendors"))
    	.andExpect(status().isOk());
    	
    }
    
}