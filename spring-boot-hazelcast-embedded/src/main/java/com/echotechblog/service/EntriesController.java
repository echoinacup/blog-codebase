package com.echotechblog.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/entries")
public class EntriesController {

    private final EntriesService service;

    public EntriesController(EntriesService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Long> getEntries() {
        return service.findAllEntries();
    }

    @PostMapping
    public ResponseEntity<String> addValue(@RequestBody EntryDto dto) {
        service.addNewEntry(dto.key(), dto.value());
        return ResponseEntity.ok("Added");
    }
}