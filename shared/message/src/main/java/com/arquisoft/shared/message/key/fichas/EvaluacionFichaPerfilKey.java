package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de EvaluacionFichaPerfil. */
public enum EvaluacionFichaPerfilKey implements MessageKey {

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
    public String bundle() {
        return MessageBundles.FICHAS;
    }
}
