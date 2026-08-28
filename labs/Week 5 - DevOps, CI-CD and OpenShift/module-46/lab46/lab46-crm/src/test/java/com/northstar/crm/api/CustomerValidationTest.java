package com.northstar.crm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerValidationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void create_rejectsInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Correlation-Id", "lab-request-001")
                        .content("""
                {"customerId":"CUS-1003","fullName":"Maya Chen",
                 "email":"bad","status":"PROSPECT"}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.correlationId").value("lab-request-001"))
                .andExpect(jsonPath("$.violations[0].field").exists());
    }

    @Test
    void get_unknownCustomer_returns404Envelope() throws Exception {
        mockMvc.perform(get("/api/customers/CUS-9999")
                        .header("X-Correlation-Id", "lab-request-001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void create_duplicateCustomer_returns409() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Correlation-Id", "lab-request-001")
                        .content("""
                {"customerId":"CUS-1001","fullName":"Amina Duplicate",
                 "email":"amina.khan@example.com","status":"ACTIVE"}
                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void get_knownCustomers_returns200() throws Exception {
        mockMvc.perform(get("/api/customers/CUS-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Amina Khan"));
        mockMvc.perform(get("/api/customers/CUS-1002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Ravi Singh"));
    }
}