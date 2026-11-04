package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoAccesoQueryOutputAdapter implements EntregableProyectoGradoAccesoQueryOutputPort {

    private final EntregableProyectoGradoAccesoQueryRepository repository;

    @Override
    public Optional<UUID> obtenerAsesorPorId(UUID entregableProyectoGrado) {
        return repository.findById(entregableProyectoGrado)
                .map(EntregableProyectoGradoAccesoJpaQueryEntity::getAsesorId);
    }
}
