package com.jerzymaj.uploadservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UploadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUploadFile() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", "audio".getBytes());

        mockMvc.perform(multipart("/too-long-to-listen/api/v1/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File uploaded successfully"));
    }

    @Test
    public void shouldReturn400_WhenExtensionNotAllowed() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "audio/mpeg", "audio".getBytes());

        mockMvc.perform(multipart("/too-long-to-listen/api/v1/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("File type not allowed"));
    }
}
