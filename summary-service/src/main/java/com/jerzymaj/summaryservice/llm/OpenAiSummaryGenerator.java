package com.jerzymaj.summaryservice.llm;

import com.jerzymaj.summaryservice.dto.SentenceResult;
import com.jerzymaj.summaryservice.event.TranscriptCompletedEvent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OpenAiSummaryGenerator implements SummaryGenerator {

    private static final String SYSTEM_PROMPT = """
            You summarize transcripts of audio recordings.
            Each input line has the format: [startMs] sentence.
            Return:
            - summary: 3 to 5 sentences capturing the main points,
            - keyMoments: the 3 to 7 most important moments, in chronological order.
              For each: startMs copied exactly from the line where the moment begins,
              a short title (max 8 words) and a one-sentence description.
            Write in the same language as the transcript.
            """;

    private final ChatClient chatClient;

    public OpenAiSummaryGenerator(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public LlmSummary generate(TranscriptCompletedEvent transcript) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(toPromptLines(transcript))
                .call()
                .entity(LlmSummary.class);
    }

    private String toPromptLines(TranscriptCompletedEvent transcript) {
        return transcript.sentences().stream()
                .map(this::toLine)
                .collect(Collectors.joining("\n"));
    }

    private String toLine(SentenceResult sentence) {
        return "[" + sentence.startMs() + "] " + sentence.text();
    }
}
