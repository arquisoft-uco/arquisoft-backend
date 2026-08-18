package com.arquisoft.shared.tracing.application.traza.primaryport;

import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;

public interface GestorTraza {

    AlcanceTraza abrir(SolicitudTraza solicitud);

    void registrarUsuario(String usuarioId);

    String correlacionActual();

    String transaccionActual();

    String usuarioActual();
}
