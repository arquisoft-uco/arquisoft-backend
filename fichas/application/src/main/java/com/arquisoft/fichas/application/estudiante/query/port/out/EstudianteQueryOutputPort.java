package com.arquisoft.fichas.application.estudiante.query.port.out;

import java.util.UUID;

public interface EstudianteQueryOutputPort {

    boolean existePorId(UUID id);
}
