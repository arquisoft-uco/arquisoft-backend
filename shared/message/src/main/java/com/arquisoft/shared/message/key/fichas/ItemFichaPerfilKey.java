package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ItemFichaPerfil. */
public enum ItemFichaPerfilKey implements ClaveMensaje {

    ERROR_TIPO_INVALIDO("fichas.dominio.itemfichaperfil.error.tipo-invalido"),
    ERROR_TIPO_DUPLICADO("fichas.dominio.itemfichaperfil.error.tipo-duplicado"),
    ERROR_FICHA_NO_AUTORIZADA("fichas.dominio.itemfichaperfil.error.ficha-no-autorizada"),
    ERROR_NO_ENCONTRADO("fichas.dominio.itemfichaperfil.error.no-encontrado"),
    ERROR_ESTADO_FICHA_NO_MODIFICABLE("fichas.dominio.itemfichaperfil.error.estado-ficha-no-modificable"),
    ERROR_CON_REVISIONES("fichas.dominio.itemfichaperfil.error.con-revisiones"),
    LOG_AGREGADO("fichas.aplicacion.itemfichaperfil.log.agregado"),
    LOG_MODIFICADO("fichas.aplicacion.itemfichaperfil.log.modificado"),
    LOG_REMOVIDO("fichas.aplicacion.itemfichaperfil.log.removido");

    private final String clave;

    ItemFichaPerfilKey(String clave) {
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
