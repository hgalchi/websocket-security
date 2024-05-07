package com.example.security;

import com.example.security.controller.LoginContoller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginContoller.class)
@ContextConfiguration
public class endPointTest {

    @Autowired
    private MockMvc mvc;

    @WithMockUser(authorities = "USER")
    @Test
    void endpointWhenUserAuthorityThenAuthorized() throws Exception {

        this.mvc.perform(get("/endPoint"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void endpointWhenNotUserAuthorityThenForbidden() throws Exception {
        this.mvc.perform(get("/endPoint"))
                .andExpect(status().isForbidden());
    }

    @Test
    void anyWhenUnauthenticatedThenUnauthorized() throws Exception {
        this.mvc.perform(get("/endPoint"))
                .andExpect(status().isOk());
    }

    @Test
    void anywhenUnauthenticatedThenUnauthorized() throws Exception {
        this.mvc.perform(get("/any"))
                .andExpect(status().isUnauthorized());
    }

}
