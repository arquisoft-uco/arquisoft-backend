package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de FichaPerfil. */
public enum FichaPerfilKey implements ClaveMensaje {

    ERROR_TITULO_DUPLICADO("fichas.dominio.fichaperfil.error.titulo-duplicado", 1),
    ERROR_ASESOR_NO_ENCONTRADO("fichas.dominio.fichaperfil.error.asesor-no-encontrado", 1),
    ERROR_NO_ENCONTRADA("fichas.dominio.fichaperfil.error.no-encontrada", 1),
    ERROR_NO_PROPIETARIO("fichas.dominio.fichaperfil.error.no-propietario", 2),
    ERROR_MISMO_ASESOR("fichas.dominio.fichaperfil.error.mismo-asesor", 1),
    ERROR_ESTADO_TERMINAL("fichas.dominio.fichaperfil.error.estado-terminal", 1),
    LOG_REGISTRADA("fichas.aplicacion.fichaperfil.log.registrada", 0),
    LOG_MODIFICADA("fichas.aplicacion.fichaperfil.log.modificada", 0),
    LOG_ASESOR_CAMBIADO("fichas.aplicacion.fichaperfil.log.asesor-cambiado", 0),
    LOG_CONSULTANDO("fichas.aplicacion.fichaperfil.log.consultando", 0),
    LOG_CONSULTA_COMPLETADA("fichas.aplicacion.fichaperfil.log.consulta-completada", 0),
    LOG_GUARDADA("fichas.infraestructura.fichaperfil.log.guardada", 0);

    private final String clave;
    private final int parametros;

    FichaPerfilKey(String clave, int parametros) {
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
