package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.secondaryport.EstudianteArtefactoQueryOutputPort;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.mapper.EstudianteArtefactoQueryMapper;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.mapper.ParticipacionArtefactoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteArtefactoQueryOutputAdapter implements EstudianteArtefactoQueryOutputPort {

    private final EstudianteArtefactoQueryRepository estudianteArtefactoQueryRepository;
    private final ParticipacionArtefactoQueryRepository participacionArtefactoQueryRepository;

    @Override
    public List<EstudianteArtefactoReadModel> consultarPorArtefacto(UUID artefacto) {
        return estudianteArtefactoQueryRepository.findByArtefactoIdOrderByNombreAscIdAsc(artefacto).stream()
                .map(EstudianteArtefactoQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<ParticipacionArtefactoReadModel> consultarParticipacionesPorEstudiante(UUID estudiante) {
        return participacionArtefactoQueryRepository
                .findByEstudianteIdOrderByNombreProyectoAscNombreDocumentoAscVersionDocumentoAscIdAsc(estudiante)
                .stream()
                .map(ParticipacionArtefactoQueryMapper::toReadModel)
                .toList();
    }
}
