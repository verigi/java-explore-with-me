package ru.practicum.publicApi.service.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.compilation.CompilationDto;
import ru.practicum.general.mapper.CompilationMapper;
import ru.practicum.general.model.Compilation;
import ru.practicum.general.repository.CompilationRepository;
import ru.practicum.general.util.ValidationHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final ValidationHandler validationHandler;

    @Autowired
    public PublicCompilationServiceImpl(CompilationRepository compilationRepository,
                                        CompilationMapper compilationMapper,
                                        ValidationHandler validationHandler) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.validationHandler = validationHandler;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        log.debug("Attempting to get compilations. Pinned: {}, from: {}, size: {}", pinned, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = pinned ? compilationRepository.findAllPinned(pageable) :
                compilationRepository.findAllUnpinned(pageable);

        if (compilations.isEmpty()) {
            log.debug("No compilations fetched. Return empty list");
            return List.of();
        }

        return compilations.stream()
                .map(compilation -> compilationMapper.toDto(compilation))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        log.debug("Attempting to get compilation. Compilation id: {}", compId);

        Compilation compilation = validationHandler.findEntityById(compilationRepository, compId, "Compilation");
        return compilationMapper.toDto(compilation);
    }
}