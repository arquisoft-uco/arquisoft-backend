package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EvaluacionFichaPerfil. */
public enum EvaluacionFichaPerfilKey implements ClaveMensaje {

    ERROR_DUPLICADA("fichas.dominio.evaluacionfichaperfil.error.duplicada", 2),
    LOG_REGISTRANDO("fichas.aplicacion.evaluacionfichaperfil.log.registrando", 2),
    LOG_VERIFICACION_REGISTRAR("fichas.aplicacion.evaluacionfichaperfil.log.verificacion-registrar", 3),
    LOG_REGISTRADA("fichas.aplicacion.evaluacionfichaperfil.log.registrada", 3),
    LOG_GUARDADA("fichas.infraestructura.evaluacionfichaperfil.log.guardada", 2);

    private final String clave;
    private final int parametros;

    EvaluacionFichaPerfilKey(String clave, int parametros) {
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
