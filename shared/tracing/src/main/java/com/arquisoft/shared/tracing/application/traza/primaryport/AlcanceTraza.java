package com.arquisoft.shared.tracing.application.traza.primaryport;

import java.util.Optional;

public interface AlcanceTraza extends AutoCloseable {

    void registrarSalida(int codigoEstado);

    String correlacionId();

    String transaccionId();

    Optional<String> traceparenteSaliente();

    @Override
    void close();
}
