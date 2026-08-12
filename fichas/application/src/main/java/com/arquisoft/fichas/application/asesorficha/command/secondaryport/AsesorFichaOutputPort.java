package com.arquisoft.fichas.application.asesorficha.command.secondaryport;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesorFicha;

import java.util.Optional;
import java.util.UUID;

public interface AsesorFichaOutputPort {

    boolean existePorId(UUID id);

    Optional<ContactoAsesorFicha> buscarContactoPorId(UUID id);
}
