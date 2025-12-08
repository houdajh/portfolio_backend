package com.devBrain.backend.controller;

import com.devBrain.backend.service.RagIndexer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag/admin")
@RequiredArgsConstructor
public class RagAdminController {

    private final RagIndexer indexer;

    @PostMapping("/reindex")
    public String reindex() throws Exception {
        indexer.reindex();
        return "Documents indexed!";
    }
}
