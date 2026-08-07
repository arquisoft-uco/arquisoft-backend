package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EvaluacionFichaPerfil. */
public enum EvaluacionFichaPerfilKey implements ClaveMensaje {

    ERROR_DUPLICADA("fichas.dominio.evaluacionfichaperfil.error.duplicada"),
    LOG_REGISTRADA("fichas.aplicacion.evaluacionfichaperfil.log.registrada");

    private final String clave;

    EvaluacionFichaPerfilKey(String clave) {
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
