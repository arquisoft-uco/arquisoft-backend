package com.arquisoft.entregables.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.application.artefacto.query.secondaryport.ArtefactoQueryOutputPort;
import com.arquisoft.entregables.infrastructure.artefacto.query.secondaryadapter.repository.mapper.ArtefactoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactoQueryOutputAdapter implements ArtefactoQueryOutputPort {

    private final ArtefactoQueryRepository artefactoQueryRepository;

    @Override
    public List<ArtefactoVistaPreviaReadModel> consultarVigentesPorProyecto(UUID proyectoGrado) {
        return artefactoQueryRepository.findByProyectoGradoIdOrderByNombreDocumentoAscIdAsc(proyectoGrado).stream()
                .map(ArtefactoQueryMapper::toReadModel)
                .toList();
    }
}
