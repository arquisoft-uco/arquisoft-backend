package com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity.EstadoEntregableProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EstadoEntregableProyectoGradoOutputPort {

    void registrar(EstadoEntregableProyectoGradoEntity estado);

    Optional<EstadoEntregableProyectoGradoEntity> obtenerEstadoActual(UUID entregableProyectoGrado);
}
