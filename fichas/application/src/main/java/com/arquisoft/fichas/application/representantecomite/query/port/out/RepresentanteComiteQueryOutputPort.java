package com.arquisoft.fichas.application.representantecomite.query.port.out;

import java.util.UUID;

public interface RepresentanteComiteQueryOutputPort {

    boolean existsById(UUID id);
}
