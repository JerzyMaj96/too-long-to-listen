package com.jerzymaj.summaryservice.controller;

import com.jerzymaj.summaryservice.dto.SummaryResult;
import com.jerzymaj.summaryservice.mapper.SummaryMapper;
import com.jerzymaj.summaryservice.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/too-long-to-listen/api/v1/summaries")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/{transcriptUuid}")
    public ResponseEntity<SummaryResult> getSummary(@PathVariable UUID transcriptUuid) {
        return ResponseEntity.ok(SummaryMapper.toDto(summaryService.getByTranscriptUuid(transcriptUuid)));
    }
}
