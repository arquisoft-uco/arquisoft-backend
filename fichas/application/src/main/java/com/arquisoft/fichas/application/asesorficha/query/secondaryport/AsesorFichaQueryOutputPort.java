package com.arquisoft.fichas.application.asesorficha.query.secondaryport;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorContactoReadModel;

import java.util.Optional;
import java.util.UUID;

public interface AsesorFichaQueryOutputPort {

    boolean existePorId(UUID id);

    Optional<AsesorContactoReadModel> buscarContactoPorId(UUID id);
}
