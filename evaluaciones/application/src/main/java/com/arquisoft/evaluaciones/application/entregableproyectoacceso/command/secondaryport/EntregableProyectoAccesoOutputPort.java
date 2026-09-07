package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.entity.EntregableProyectoAccesoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EntregableProyectoAccesoOutputPort {

    Optional<EntregableProyectoAccesoEntity> buscarPorEntregable(UUID entregable);

    void guardar(EntregableProyectoAccesoEntity entity);
}
