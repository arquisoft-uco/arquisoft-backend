package com.arquisoft.entregables.application.asesorproyectogrado.query.secondaryport;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoAccesoQueryOutputPort {

    Optional<UUID> obtenerAsesorVigentePorProyectoGrado(UUID proyectoGrado);
}
