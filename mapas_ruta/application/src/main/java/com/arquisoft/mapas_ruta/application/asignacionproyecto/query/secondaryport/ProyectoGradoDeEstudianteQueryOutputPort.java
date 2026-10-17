package com.arquisoft.mapas_ruta.application.asignacionproyecto.query.secondaryport;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoDeEstudianteQueryOutputPort {

    Optional<UUID> obtenerProyectoGrado(UUID estudiante);
}
