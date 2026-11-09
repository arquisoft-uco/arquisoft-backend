package com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport;

import java.util.Optional;
import java.util.UUID;

public interface EntregableProyectoGradoAccesoQueryOutputPort {

    Optional<UUID> obtenerAsesorPorId(UUID entregableProyectoGrado);

    boolean estudianteParticipa(UUID entregableProyectoGrado, UUID estudiante);
}
