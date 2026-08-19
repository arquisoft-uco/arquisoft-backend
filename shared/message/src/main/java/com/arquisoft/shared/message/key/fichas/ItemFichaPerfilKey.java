package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ItemFichaPerfil. */
public enum ItemFichaPerfilKey implements ClaveMensaje {

    ERROR_TIPO_INVALIDO("fichas.dominio.itemfichaperfil.error.tipo-invalido", 1),
    ERROR_TIPO_DUPLICADO("fichas.dominio.itemfichaperfil.error.tipo-duplicado", 1),
    ERROR_FICHA_NO_AUTORIZADA("fichas.dominio.itemfichaperfil.error.ficha-no-autorizada", 1),
    ERROR_NO_ENCONTRADO("fichas.dominio.itemfichaperfil.error.no-encontrado", 1),
    ERROR_ESTADO_FICHA_NO_MODIFICABLE("fichas.dominio.itemfichaperfil.error.estado-ficha-no-modificable", 1),
    ERROR_CON_REVISIONES("fichas.dominio.itemfichaperfil.error.con-revisiones", 1),
    LOG_AGREGADO("fichas.aplicacion.itemfichaperfil.log.agregado", 0),
    LOG_MODIFICADO("fichas.aplicacion.itemfichaperfil.log.modificado", 0),
    LOG_REMOVIDO("fichas.aplicacion.itemfichaperfil.log.removido", 0);

    private final String clave;
    private final int parametros;

    ItemFichaPerfilKey(String clave, int parametros) {
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
