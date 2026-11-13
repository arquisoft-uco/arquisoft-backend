package com.arquisoft.biblioteca.application.documentacion.query.secondaryport;

import java.util.Optional;
import java.util.UUID;

public interface DocumentacionAccesoQueryOutputPort {

    Optional<String> obtenerEstadoPorId(UUID documentacion);
}
