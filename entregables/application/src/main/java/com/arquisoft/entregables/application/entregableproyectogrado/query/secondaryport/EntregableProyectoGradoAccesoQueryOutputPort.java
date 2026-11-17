package com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport;

import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.entity.EntregableProyectoGradoAccesoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EntregableProyectoGradoAccesoQueryOutputPort {

    Optional<UUID> obtenerAsesorVigentePorId(UUID entregableProyectoGrado);

    Optional<EntregableProyectoGradoAccesoEntity> obtenerAccesoPorId(UUID entregableProyectoGrado);

    boolean estudianteParticipa(UUID entregableProyectoGrado, UUID estudiante);
}
