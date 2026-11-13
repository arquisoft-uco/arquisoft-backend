package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.EstadoEntregableProyectoGradoQueryOutputPort;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository.mapper.EstadoEntregableProyectoGradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoEntregableProyectoGradoQueryOutputAdapter implements EstadoEntregableProyectoGradoQueryOutputPort {

    private final EstadoEntregableProyectoGradoQueryRepository estadoEntregableProyectoGradoQueryRepository;

    @Override
    public List<EstadoEntregableProyectoGradoReadModel> consultarPorEntregable(UUID entregableProyectoGrado) {
        return estadoEntregableProyectoGradoQueryRepository
                .findByEntregableProyectoGradoIdOrderByFechaActualizacionAsc(entregableProyectoGrado).stream()
                .map(EstadoEntregableProyectoGradoQueryMapper::toReadModel)
                .toList();
    }
}
