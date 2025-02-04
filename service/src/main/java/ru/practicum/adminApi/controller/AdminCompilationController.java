package ru.practicum.adminApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminApi.service.compilation.AdminCompilationService;
import ru.practicum.general.dto.compilation.CompilationDto;
import ru.practicum.general.dto.compilation.CreateCompilationDto;
import ru.practicum.general.dto.compilation.UpdateCompilationDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final AdminCompilationService service;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody CreateCompilationDto createCompilationDto) {
        log.debug("New POST request received. Compilation title: ", createCompilationDto.getTitle());
        //ex 400 BAD_REQUEST
        //ex 409 CONFLICT
        CompilationDto compilationDto = service.createCompilation(createCompilationDto); // тут будет сервис
        log.debug("Compilation \"{}\" successfully saved", compilationDto.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationDto);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable("compId") Long compId) {
        log.debug("New DELETE request received. Category id: {}", compId);
        //ex 404 NOT_FOUND
        service.deleteCompilation(compId); // тут будет сервис
        log.debug("Compilation with id {} successfully deleted", compId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable("compId") Long compId,
                                                            @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {
        log.debug("New PATCH request received. Compilation id: {}", compId);
        CompilationDto compilationDto = service.updateCompilation(compId, updateCompilationDto); // тут будет сервис
        log.debug("Compilation \"{}\" successfully updated", compilationDto.getTitle());
        return ResponseEntity.status(HttpStatus.OK).body(compilationDto);
    }
}