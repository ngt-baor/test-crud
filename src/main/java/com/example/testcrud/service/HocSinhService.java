package com.example.testcrud.service;

import com.example.testcrud.dto.HocSinhRequest;
import com.example.testcrud.dto.HocSinhResponse;
import com.example.testcrud.repository.HocSinhRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

@Service
public class HocSinhService {
    private static final DateTimeFormatter STRICT_DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("MM/dd/uuuu")
            .toFormatter(Locale.US)
            .withResolverStyle(ResolverStyle.STRICT);

    private final HocSinhRepository repository;

    public HocSinhService(HocSinhRepository repository) {
        this.repository = repository;
    }

    public List<HocSinhResponse> findAll() {
        return repository.findAll();
    }

    public HocSinhResponse findById(int maHS) {
        return repository.findById(maHS).orElseThrow(() -> notFound("Không tìm thấy học sinh"));
    }

    public HocSinhResponse create(HocSinhRequest request) {
        ensureClassExists(request.maLop());
        return repository.insert(request, parseDate(request.ngaySinh()));
    }

    public HocSinhResponse update(int maHS, HocSinhRequest request) {
        ensureClassExists(request.maLop());
        return repository.update(maHS, request, parseDate(request.ngaySinh()))
                .orElseThrow(() -> notFound("Không tìm thấy học sinh"));
    }

    public void delete(int maHS) {
        if (!repository.delete(maHS)) {
            throw notFound("Không tìm thấy học sinh");
        }
    }

    private void ensureClassExists(int maLop) {
        if (!repository.classExists(maLop)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MaLop không tồn tại");
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value, STRICT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "NgaySinh không phải ngày hợp lệ theo định dạng MM/dd/yyyy");
        }
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}