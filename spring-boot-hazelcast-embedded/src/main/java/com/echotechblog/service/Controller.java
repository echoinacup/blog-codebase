package com.echotechblog.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/entries")
public class Controller {
    private static final Map<String, Long> DB_MOCK = Map.of("one", 1L, "two", 2L, "tree", 3L);

    @GetMapping
    public Map<String, Long> getEntries() {
        return DB_MOCK;
    }
}
