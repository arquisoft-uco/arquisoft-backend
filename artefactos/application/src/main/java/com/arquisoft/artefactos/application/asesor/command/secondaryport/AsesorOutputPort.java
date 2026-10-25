package com.arquisoft.artefactos.application.asesor.command.secondaryport;

import com.arquisoft.artefactos.application.asesor.command.secondaryport.entity.AsesorEntity;

import java.util.Optional;
import java.util.UUID;

public interface AsesorOutputPort {

    Optional<AsesorEntity> obtenerPorRevisionAsesor(UUID revisionAsesor);
}
