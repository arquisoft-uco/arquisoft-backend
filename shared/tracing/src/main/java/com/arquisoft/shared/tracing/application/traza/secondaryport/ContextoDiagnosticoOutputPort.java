package com.arquisoft.shared.tracing.application.traza.secondaryport;

import com.arquisoft.shared.tracing.domain.traza.TrazaDomain;
import com.arquisoft.shared.tracing.domain.traza.model.SalidaTraza;

import java.util.Map;

public interface ContextoDiagnosticoOutputPort {

    Map<String, String> capturar();

    void escribirTraza(TrazaDomain traza);

    void escribirUsuario(String usuarioId);

    void escribirSalida(SalidaTraza salida);

    void restaurar(Map<String, String> contextoPrevio);

    String leerCorrelacion();

    String leerTransaccion();

    String leerUsuario();
}
