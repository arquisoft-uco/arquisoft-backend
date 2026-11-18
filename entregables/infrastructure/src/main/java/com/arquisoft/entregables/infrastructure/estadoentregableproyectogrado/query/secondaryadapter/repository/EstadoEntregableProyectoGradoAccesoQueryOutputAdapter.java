package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.EstadoEntregableProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.entity.EstadoEntregableProyectoGradoAccesoEntity;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository.mapper.EstadoEntregableProyectoGradoAccesoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoEntregableProyectoGradoAccesoQueryOutputAdapter
        implements EstadoEntregableProyectoGradoAccesoQueryOutputPort {

    private final EstadoEntregableProyectoGradoAccesoQueryRepository repository;

    @Override
    public Optional<EstadoEntregableProyectoGradoAccesoEntity> obtenerEstadoActual(UUID entregableProyectoGrado) {
        return repository.findFirstByEntregableProyectoGradoIdOrderByFechaActualizacionDesc(entregableProyectoGrado)
                .map(EstadoEntregableProyectoGradoAccesoQueryMapper::toEntity);
    }
}
