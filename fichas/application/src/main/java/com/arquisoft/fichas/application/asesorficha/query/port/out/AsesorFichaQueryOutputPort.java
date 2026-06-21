package com.arquisoft.fichas.application.asesorficha.query.port.out;

import java.util.UUID;

public interface AsesorFichaQueryOutputPort {

    boolean existsById(UUID id);
}
