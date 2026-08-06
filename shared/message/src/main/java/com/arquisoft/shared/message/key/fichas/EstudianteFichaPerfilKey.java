package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de EstudianteFichaPerfil. */
public enum EstudianteFichaPerfilKey implements MessageKey {

    ERROR_DUPLICADO("fichas.dominio.estudiantefichaperfil.error.duplicado"),
    ERROR_LIMITE_EXCEDIDO("fichas.dominio.estudiantefichaperfil.error.limite-excedido"),
    ERROR_RELACION_NO_ENCONTRADA("fichas.dominio.estudiantefichaperfil.error.relacion-no-encontrada"),
    LOG_ASIGNADO("fichas.aplicacion.estudiantefichaperfil.log.asignado"),
    LOG_REMOVIDO("fichas.aplicacion.estudiantefichaperfil.log.removido");

    private final String clave;

    EstudianteFichaPerfilKey(String clave) {
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
