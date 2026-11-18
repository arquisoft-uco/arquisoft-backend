package com.arquisoft.entregables.infrastructure.paqueteentregable.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.PaqueteEntregableAccesoQueryOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.entity.PaqueteEntregableAccesoEntity;
import com.arquisoft.entregables.infrastructure.paqueteentregable.query.secondaryadapter.repository.mapper.PaqueteEntregableAccesoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaqueteEntregableAccesoQueryOutputAdapter implements PaqueteEntregableAccesoQueryOutputPort {

    private final PaqueteEntregableAccesoQueryRepository repository;

    @Override
    public Optional<PaqueteEntregableAccesoEntity> obtenerPorEntregable(UUID entregableProyectoGrado) {
        return repository.findByEntregableProyectoGradoId(entregableProyectoGrado)
                .map(PaqueteEntregableAccesoQueryMapper::toEntity);
    }
}
