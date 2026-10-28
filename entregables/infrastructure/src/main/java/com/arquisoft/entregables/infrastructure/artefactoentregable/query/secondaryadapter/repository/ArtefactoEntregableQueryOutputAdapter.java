package com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.entregables.application.artefactoentregable.query.secondaryport.ArtefactoEntregableQueryOutputPort;
import com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository.mapper.ArtefactoEntregableQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactoEntregableQueryOutputAdapter implements ArtefactoEntregableQueryOutputPort {

    private final ArtefactoEntregableQueryRepository artefactoEntregableQueryRepository;

    @Override
    public List<ArtefactoEntregableReadModel> consultarPorEntregable(UUID entregableProyectoGrado) {
        return artefactoEntregableQueryRepository
                .findByEntregableIdOrderByNombreDocumentoAscIdAsc(entregableProyectoGrado).stream()
                .map(ArtefactoEntregableQueryMapper::toReadModel)
                .toList();
    }
}
