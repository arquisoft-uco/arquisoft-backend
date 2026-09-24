package com.arquisoft.proyectos.application.asesor.command.secondaryport;

import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;

import java.util.Optional;
import java.util.UUID;

public interface AsesorOutputPort {

    void guardar(AsesorEntity asesor);

    Optional<AsesorEntity> obtenerPorId(UUID id);

    void actualizar(AsesorEntity asesor);
}
