package ru.practicum.publicApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.general.dto.compilation.CompilationDto;
import ru.practicum.publicApi.service.compilation.PublicCompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final PublicCompilationService service;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(required = false, defaultValue = "false") boolean pinned,
                                                                @RequestParam(defaultValue = "0") int from,
                                                                @RequestParam(defaultValue = "10") int size) {
        log.debug("New GET request received. Params: pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> compilationDtos = service.getCompilations(pinned, from, size);
        if (compilationDtos.isEmpty()) {
            log.debug("No compilations fetched. Return empty list");
        } else {
            log.debug("Compilation successfully fetched. Count: {}", compilationDtos.size());
        }
        return ResponseEntity.status(HttpStatus.OK).body(compilationDtos);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable("compId") Long compId) {
        log.debug("New GET request received. Compilation id: {}", compId);
        CompilationDto compilationDto = service.getCompilation(compId);
        return ResponseEntity.status(HttpStatus.OK).body(compilationDto);
    }
}