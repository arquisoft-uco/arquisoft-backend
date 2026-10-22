package com.arquisoft.evaluaciones.application.jurado.query.secondaryport;

import java.util.UUID;

public interface JuradoAccesoQueryOutputPort {

    boolean existePorId(UUID jurado);
}
