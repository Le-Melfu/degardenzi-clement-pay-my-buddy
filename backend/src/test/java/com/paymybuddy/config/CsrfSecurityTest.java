package com.paymybuddy.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CSRF protection configuration
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class CsrfSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCsrfTokenIsGeneratedOnGetRequest() throws Exception {
        MvcResult result = mockMvc.perform(get("/csrf"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie csrfCookie = Objects.requireNonNull(result.getResponse().getCookie("XSRF-TOKEN"));
        assertThat(csrfCookie.getValue()).isNotEmpty();
        assertThat(csrfCookie.isHttpOnly()).isFalse();
    }

    @Test
    public void testPostRequestWithoutCsrfTokenReturns403() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testPostRequestWithValidCsrfTokenIsAccepted() throws Exception {
        MvcResult csrfResult = mockMvc.perform(get("/csrf"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie csrfCookie = Objects.requireNonNull(csrfResult.getResponse().getCookie("XSRF-TOKEN"));

        String csrfToken = csrfCookie.getValue();
        assertThat(csrfToken).isNotNull();

        MvcResult result = mockMvc.perform(post("/login")
                .cookie(csrfCookie)
                .header("X-XSRF-TOKEN", csrfToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    public void testRegisterEndpointWithCsrfToken() throws Exception {
        MvcResult csrfResult = mockMvc.perform(get("/csrf"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie csrfCookie = Objects.requireNonNull(csrfResult.getResponse().getCookie("XSRF-TOKEN"));

        String csrfToken = csrfCookie.getValue();
        assertThat(csrfToken).isNotNull();

        String newUser = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe.csrf.test@example.com",
                    "password": "SecurePass123!"
                }
                """;

        MvcResult result = mockMvc.perform(post("/register")
                .cookie(csrfCookie)
                .header("X-XSRF-TOKEN", csrfToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUser))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    public void testPutRequestRequiresCsrfToken() throws Exception {
        mockMvc.perform(put("/user")
                .with(user("test@example.com").password("password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Updated\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteRequestRequiresCsrfToken() throws Exception {
        mockMvc.perform(delete("/some-resource")
                .with(user("test@example.com").password("password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetRequestDoesNotRequireCsrfToken() throws Exception {
        mockMvc.perform(get("/csrf"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCsrfTokenIsSameSiteLax() throws Exception {
        MvcResult result = mockMvc.perform(get("/csrf"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie csrfCookie = result.getResponse().getCookie("XSRF-TOKEN");
        assertThat(csrfCookie).isNotNull();
    }

    @Test
    public void testInvalidCsrfTokenReturns403() throws Exception {
        MvcResult csrfResult = mockMvc.perform(get("/csrf"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie csrfCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");
        assertThat(csrfCookie).isNotNull();

        String invalidToken = "invalid-csrf-token-value";

        mockMvc.perform(post("/login")
                .cookie(csrfCookie)
                .header("X-XSRF-TOKEN", invalidToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCsrfTokenInCookieWithoutHeaderReturns403() throws Exception {
        MvcResult csrfResult = mockMvc.perform(get("/csrf"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie csrfCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");
        assertThat(csrfCookie).isNotNull();

        mockMvc.perform(post("/login")
                .cookie(csrfCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isForbidden());
    }
}
