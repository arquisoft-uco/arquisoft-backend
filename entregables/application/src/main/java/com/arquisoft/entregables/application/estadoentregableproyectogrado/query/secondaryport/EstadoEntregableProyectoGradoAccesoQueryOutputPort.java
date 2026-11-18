package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.entity.EstadoEntregableProyectoGradoAccesoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EstadoEntregableProyectoGradoAccesoQueryOutputPort {

    Optional<EstadoEntregableProyectoGradoAccesoEntity> obtenerEstadoActual(UUID entregableProyectoGrado);
}
