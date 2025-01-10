package ru.practicum.open.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.compilation.CompilationDto;
import ru.practicum.open.service.compilation.OpenCompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class OpenCompilationController {
    private final OpenCompilationService service;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getByParam(@RequestParam(required = false) boolean pinned,
                                                           @RequestParam(defaultValue = "0") int from,
                                                           @RequestParam(defaultValue = "10") int size) {
        log.debug("New GET request received. Params: pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> compilationDtos = null;
        // ex 400 BAD_REQUEST
        if (compilationDtos.isEmpty()) {
            log.debug("No compilations fetched. Return empty list");
        } else {
            log.debug("Compilation successfully fetched. Count: {}", compilationDtos.size());
        }
        return ResponseEntity.status(HttpStatus.OK).body(compilationDtos);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getById(@PathVariable("compId") Long compId) {
        log.debug("New GET request received. Compilation id: {}", compId);
        CompilationDto compilationDto = null; // Тут будет сервис
        // ex 400 BAD_REQUEST
        return ResponseEntity.status(HttpStatus.OK).body(compilationDto);
    }
}