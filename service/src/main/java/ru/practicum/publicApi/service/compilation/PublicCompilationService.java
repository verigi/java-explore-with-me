package ru.practicum.publicApi.service.compilation;


import ru.practicum.general.dto.compilation.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilations(boolean pinned, int from, int size);

    CompilationDto getCompilation(Long compId);
}