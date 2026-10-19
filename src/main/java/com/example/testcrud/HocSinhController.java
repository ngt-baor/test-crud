package com.example.testcrud;

import com.example.testcrud.dto.HocSinhRequest;
import com.example.testcrud.dto.HocSinhResponse;
import com.example.testcrud.service.HocSinhService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/hoc-sinh")
public class HocSinhController {
    private final HocSinhService service;

    public HocSinhController(HocSinhService service) {
        this.service = service;
    }

    @GetMapping
    public List<HocSinhResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{maHS}")
    public HocSinhResponse findById(@PathVariable int maHS) {
        return service.findById(maHS);
    }

    @PostMapping
    public ResponseEntity<HocSinhResponse> create(@Valid @RequestBody HocSinhRequest request) {
        HocSinhResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/hoc-sinh/" + created.maHS())).body(created);
    }

    @PutMapping("/{maHS}")
    public HocSinhResponse update(@PathVariable int maHS, @Valid @RequestBody HocSinhRequest request) {
        return service.update(maHS, request);
    }

    @DeleteMapping("/{maHS}")
    public ResponseEntity<Void> delete(@PathVariable int maHS) {
        service.delete(maHS);
        return ResponseEntity.noContent().build();
    }
}