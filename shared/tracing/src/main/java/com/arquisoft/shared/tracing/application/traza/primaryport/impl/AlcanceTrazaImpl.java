package com.arquisoft.shared.tracing.application.traza.primaryport.impl;

import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.secondaryport.ContextoDiagnosticoOutputPort;
import com.arquisoft.shared.tracing.domain.traza.TrazaDomain;

import java.util.Map;
import java.util.Optional;

final class AlcanceTrazaImpl implements AlcanceTraza {

    private final ContextoDiagnosticoOutputPort contexto;
    private final TrazaDomain traza;
    private final Map<String, String> contextoPrevio;

    AlcanceTrazaImpl(final ContextoDiagnosticoOutputPort contexto, final TrazaDomain traza) {
        this.contexto = contexto;
        this.traza = traza;
        this.contextoPrevio = contexto.capturar();
        contexto.escribirTraza(traza);
    }

    @Override
    public void registrarSalida(final int codigoEstado) {
        contexto.escribirSalida(traza.registrarSalida(codigoEstado));
    }

    @Override
    public String correlacionId() {
        return traza.getCorrelacionId();
    }

    @Override
    public String transaccionId() {
        return traza.getTransaccionId();
    }

    @Override
    public Optional<String> traceparenteSaliente() {
        return traza.traceparenteSaliente();
    }

    @Override
    public void close() {
        contexto.restaurar(contextoPrevio);
    }
}
