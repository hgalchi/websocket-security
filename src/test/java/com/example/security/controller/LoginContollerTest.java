package com.example.security.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class LoginContollerTest {

    @Autowired
    MockMvc mockMvc;

    @WithMockUser(authorities = "CUSTOMER")
    @Test
    void endpointFailWithForb() throws Exception {
        this.mockMvc.perform(get("/endpoint"))
                .andExpect(status().isForbidden());
    }


    @WithMockUser(authorities = "USER")
    @Test
    void endpointSucc() throws Exception {
        this.mockMvc.perform(get("/endpoint"))
                .andExpect(status().isOk());
    }


    @Test
    void endpointFailWithNotAuthorication() throws Exception {
        this.mockMvc.perform(get("/endpoint"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("USER")
    void resourcePageWithSucc() throws Exception {
        this.mockMvc.perform(get("/resource/authentication"))
                .andExpect(status().isOk());
    }

    @WithMockUser(authorities = "read")
    @Test
    void getWhenReadAuth() throws Exception {
        this.mockMvc.perform(post("/any"))
                .andExpect(status().isOk());

    }

    @WithMockUser(authorities = "writer")
    @Test
    void getWhenwriterAuth() throws Exception {
        this.mockMvc.perform(get("/any"))
                .andExpect(status().isOk());

    }

    @WithMockUser(authorities = "print")
    @Test
    void printWhenPrintAuth() throws Exception {
        this.mockMvc.perform(get("/any?print"))
                .andExpect(status().isOk());
    }

    @WithMockUser(authorities = "print")
    @Test
    void printWhenPrintAuth_fail() throws Exception {
        this.mockMvc.perform(get("/any"))
                .andExpect(status().isOk());
    }
}