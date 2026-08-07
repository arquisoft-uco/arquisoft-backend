package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstudianteFichaPerfil. */
public enum EstudianteFichaPerfilKey implements ClaveMensaje {

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
    public String paquete() {
        return PaquetesMensajes.FICHAS;
    }
}
