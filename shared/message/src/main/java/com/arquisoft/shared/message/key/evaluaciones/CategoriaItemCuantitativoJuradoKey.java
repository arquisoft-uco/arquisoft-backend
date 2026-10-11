package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CategoriaItemCuantitativoJuradoKey implements ClaveMensaje {

    LOG_CONSULTANDO("evaluaciones.aplicacion.categoriaitemcuantitativojurado.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA(
            "evaluaciones.aplicacion.categoriaitemcuantitativojurado.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    CategoriaItemCuantitativoJuradoKey(String clave, int parametros) {
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
