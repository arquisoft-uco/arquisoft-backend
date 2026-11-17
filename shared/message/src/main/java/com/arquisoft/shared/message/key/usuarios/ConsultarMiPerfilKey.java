package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultarMiPerfilKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.usuario.log.consultando-mi-perfil", 1),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.usuario.log.consulta-mi-perfil-completada", 2),
    ERROR_PERFIL_NO_DISPONIBLE("usuarios.infraestructura.usuario.error.perfil-no-disponible", 0),
    ERROR_PERFIL_NO_DISPONIBLE_DETALLE("usuarios.infraestructura.usuario.error.perfil-no-disponible-detalle", 1);

    private final String clave;
    private final int parametros;

    ConsultarMiPerfilKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
