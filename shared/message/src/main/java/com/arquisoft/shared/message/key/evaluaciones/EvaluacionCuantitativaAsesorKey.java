package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionCuantitativaAsesorKey implements ClaveMensaje {

    ERROR_ITEMS_NO_ENCONTRADOS(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.items-no-encontrados", 1),
    ERROR_PUNTAJES_EXCEDEN_VALOR_ITEM(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.puntajes-exceden-valor-item", 1),
    ERROR_ITEMS_YA_REGISTRADOS(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.items-ya-registrados", 1),
    LOG_REGISTRANDO_LOTE("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.registrando-lote", 2),
    LOG_VERIFICACION_LOTE("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.verificacion-lote", 4),
    LOG_LOTE_REGISTRADO("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.lote-registrado", 3),
    LOG_LOTE_GUARDADO("evaluaciones.infraestructura.evaluacioncuantitativaasesor.log.lote-guardado", 1);

    private final String clave;
    private final int parametros;

    EvaluacionCuantitativaAsesorKey(String clave, int parametros) {
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
