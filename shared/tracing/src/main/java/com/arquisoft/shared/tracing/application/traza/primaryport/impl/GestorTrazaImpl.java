package com.arquisoft.shared.tracing.application.traza.primaryport.impl;

import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.application.traza.secondaryport.ContextoDiagnosticoOutputPort;
import com.arquisoft.shared.tracing.domain.traza.TrazaDomain;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.util.UtilTexto;

public class GestorTrazaImpl implements GestorTraza {

    private final ContextoDiagnosticoOutputPort contexto;
    private final boolean anonimizarIp;

    public GestorTrazaImpl(final ContextoDiagnosticoOutputPort contexto, final boolean anonimizarIp) {
        this.contexto = contexto;
        this.anonimizarIp = anonimizarIp;
    }

    @Override
    public AlcanceTraza abrir(final SolicitudTraza solicitud) {
        return new AlcanceTrazaImpl(contexto, TrazaDomain.crear(solicitud, anonimizarIp));
    }

    @Override
    public void registrarUsuario(final String usuarioId) {
        if (!UtilTexto.esVacioONulo(usuarioId)) {
            contexto.escribirUsuario(usuarioId);
        }
    }

    @Override
    public String correlacionActual() {
        return contexto.leerCorrelacion();
    }

    @Override
    public String transaccionActual() {
        return contexto.leerTransaccion();
    }

    @Override
    public String usuarioActual() {
        return contexto.leerUsuario();
    }
}
