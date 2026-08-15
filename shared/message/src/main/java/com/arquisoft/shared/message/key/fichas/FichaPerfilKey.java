package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de FichaPerfil. */
public enum FichaPerfilKey implements ClaveMensaje {

    ERROR_TITULO_DUPLICADO("fichas.dominio.fichaperfil.error.titulo-duplicado"),
    ERROR_ASESOR_NO_ENCONTRADO("fichas.dominio.fichaperfil.error.asesor-no-encontrado"),
    ERROR_NO_ENCONTRADA("fichas.dominio.fichaperfil.error.no-encontrada"),
    ERROR_NO_PROPIETARIO("fichas.dominio.fichaperfil.error.no-propietario"),
    ERROR_MISMO_ASESOR("fichas.dominio.fichaperfil.error.mismo-asesor"),
    ERROR_ESTADO_TERMINAL("fichas.dominio.fichaperfil.error.estado-terminal"),
    LOG_REGISTRADA("fichas.aplicacion.fichaperfil.log.registrada"),
    LOG_MODIFICADA("fichas.aplicacion.fichaperfil.log.modificada"),
    LOG_ASESOR_CAMBIADO("fichas.aplicacion.fichaperfil.log.asesor-cambiado"),
    LOG_CONSULTANDO("fichas.aplicacion.fichaperfil.log.consultando"),
    LOG_CONSULTA_COMPLETADA("fichas.aplicacion.fichaperfil.log.consulta-completada"),
    LOG_GUARDADA("fichas.infraestructura.fichaperfil.log.guardada");

    private final String clave;

    FichaPerfilKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.FICHAS;
    }
}
