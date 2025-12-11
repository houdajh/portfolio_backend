package com.devBrain.backend.controller;

import com.devBrain.backend.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    public record RagRequest(String question, Integer topK) {
    }

    public record RagHttpResponse(String answer, List<RagService.RagSource> sources) {
    }

    @PostMapping("/ask")
    public RagHttpResponse ask(@RequestBody RagRequest payload) {
        int topK = payload.topK() != null ? payload.topK() : 5;
        var response = ragService.ask(payload.question(), topK);
        return new RagHttpResponse(response.answer(), response.sources());
    }
}
