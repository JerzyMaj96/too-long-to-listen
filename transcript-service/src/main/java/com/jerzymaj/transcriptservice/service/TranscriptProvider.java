package com.jerzymaj.transcriptservice.service;

import com.jerzymaj.transcriptservice.model.Transcript;

import java.io.IOException;

public interface TranscriptProvider {

    Transcript processAudioFile(String uploadPath) throws IOException;
}
