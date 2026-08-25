package com.arquisoft.fichas.application.asesorficha.command.secondaryport;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;

import java.util.Optional;
import java.util.UUID;

public interface AsesorFichaOutputPort {

    boolean existePorId(UUID id);

    Optional<AsesorFichaEntity> buscarContactoPorId(UUID id);
}
