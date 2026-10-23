package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.secondaryport.EstudianteArtefactoQueryOutputPort;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.mapper.EstudianteArtefactoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteArtefactoQueryOutputAdapter implements EstudianteArtefactoQueryOutputPort {

    private final EstudianteArtefactoQueryRepository estudianteArtefactoQueryRepository;

    @Override
    public List<EstudianteArtefactoReadModel> consultarPorArtefacto(UUID artefacto) {
        return estudianteArtefactoQueryRepository.findByArtefactoIdOrderByNombreAscIdAsc(artefacto).stream()
                .map(EstudianteArtefactoQueryMapper::toReadModel)
                .toList();
    }
}
