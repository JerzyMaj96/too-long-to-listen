package com.jerzymaj.transcriptservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AssemblyUploadResponse(@JsonProperty("upload_url") String uploadUrl) {
}
