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
    LOG_REGISTRANDO("fichas.aplicacion.fichaperfil.log.registrando", 2),
    LOG_VERIFICACION_PREVIA("fichas.aplicacion.fichaperfil.log.verificacion-previa", 3),
    LOG_REGISTRADA("fichas.aplicacion.fichaperfil.log.registrada", 1),
    LOG_MODIFICANDO("fichas.aplicacion.fichaperfil.log.modificando", 2),
    LOG_VERIFICACION_MODIFICAR("fichas.aplicacion.fichaperfil.log.verificacion-modificar", 2),
    LOG_MODIFICADA("fichas.aplicacion.fichaperfil.log.modificada", 1),
    LOG_CAMBIANDO_ASESOR("fichas.aplicacion.fichaperfil.log.cambiando-asesor", 2),
    LOG_VERIFICACION_CAMBIO_ASESOR("fichas.aplicacion.fichaperfil.log.verificacion-cambio-asesor", 3),
    LOG_ASESOR_CAMBIADO("fichas.aplicacion.fichaperfil.log.asesor-cambiado", 2),
    LOG_CONSULTANDO("fichas.aplicacion.fichaperfil.log.consultando", 4),
    LOG_CONSULTA_COMPLETADA("fichas.aplicacion.fichaperfil.log.consulta-completada", 3),
    LOG_GUARDADA("fichas.infraestructura.fichaperfil.log.guardada", 1);

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
