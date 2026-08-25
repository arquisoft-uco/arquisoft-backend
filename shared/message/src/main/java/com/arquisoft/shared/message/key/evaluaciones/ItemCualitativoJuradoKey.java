package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ItemCualitativoJuradoKey implements ClaveMensaje {

    ERROR_NOMBRE_DUPLICADO("evaluaciones.dominio.itemcualitativojurado.error.nombre-duplicado", 1),
    ERROR_PERSISTENCIA("evaluaciones.infraestructura.itemcualitativojurado.error.persistencia", 0),
    LOG_REGISTRADO("evaluaciones.aplicacion.itemcualitativojurado.log.registrado", 1);

    private final String clave;
    private final int parametros;

    ItemCualitativoJuradoKey(String clave, int parametros) {
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
