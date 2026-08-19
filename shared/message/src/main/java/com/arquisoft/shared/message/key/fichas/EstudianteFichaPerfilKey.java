package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstudianteFichaPerfil. */
public enum EstudianteFichaPerfilKey implements ClaveMensaje {

    ERROR_DUPLICADO("fichas.dominio.estudiantefichaperfil.error.duplicado", 1),
    ERROR_LIMITE_EXCEDIDO("fichas.dominio.estudiantefichaperfil.error.limite-excedido", 1),
    ERROR_RELACION_NO_ENCONTRADA("fichas.dominio.estudiantefichaperfil.error.relacion-no-encontrada", 2),
    LOG_ASIGNADO("fichas.aplicacion.estudiantefichaperfil.log.asignado", 0),
    LOG_REMOVIDO("fichas.aplicacion.estudiantefichaperfil.log.removido", 0);

    private final String clave;
    private final int parametros;

    EstudianteFichaPerfilKey(String clave, int parametros) {
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
