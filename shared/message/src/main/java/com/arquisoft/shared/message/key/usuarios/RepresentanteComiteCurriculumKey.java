package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RepresentanteComiteCurriculum. */
public enum RepresentanteComiteCurriculumKey implements ClaveMensaje {

    ERROR_USUARIO_NO_ENCONTRADO("usuarios.dominio.representantecomitecurriculum.error.usuario-no-encontrado", 1),
    ERROR_USUARIO_YA_ES_REPRESENTANTE("usuarios.dominio.representantecomitecurriculum.error.usuario-ya-es-representante", 1),
    LOG_AGREGADO("usuarios.aplicacion.representantecomitecurriculum.log.agregado", 2);

    private final String clave;
    private final int parametros;

    RepresentanteComiteCurriculumKey(String clave, int parametros) {
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
