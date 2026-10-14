package com.arquisoft.proyectos.application.proyectogrado.query.secondaryport;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoAccesoQueryOutputPort {

    Optional<UUID> obtenerCoordinadorPorId(UUID proyectoGrado);

    boolean existePorId(UUID proyectoGrado);
}
