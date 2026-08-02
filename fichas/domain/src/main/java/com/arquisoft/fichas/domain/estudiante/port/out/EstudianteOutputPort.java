package com.arquisoft.fichas.domain.estudiante.port.out;

import java.util.UUID;

public interface EstudianteOutputPort {

    boolean existePorId(UUID id);
}
