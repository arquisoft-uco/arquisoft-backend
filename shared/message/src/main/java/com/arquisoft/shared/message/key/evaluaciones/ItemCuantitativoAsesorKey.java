package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ItemCuantitativoAsesorKey implements ClaveMensaje {

    ERROR_CATEGORIA_NO_ENCONTRADA(
            "evaluaciones.dominio.itemcuantitativoasesor.error.categoria-no-encontrada", 0),
    ERROR_CATEGORIA_NO_ENCONTRADA_DETALLE(
            "evaluaciones.dominio.itemcuantitativoasesor.error.categoria-no-encontrada-detalle", 1),
    ERROR_NOMBRE_CATEGORIA_DUPLICADO(
            "evaluaciones.dominio.itemcuantitativoasesor.error.nombre-categoria-duplicado", 1),
    ERROR_NOMBRE_CATEGORIA_DUPLICADO_DETALLE(
            "evaluaciones.dominio.itemcuantitativoasesor.error.nombre-categoria-duplicado-detalle", 2),
    LOG_REGISTRANDO("evaluaciones.aplicacion.itemcuantitativoasesor.log.registrando", 2),
    LOG_VERIFICACION_REGISTRAR(
            "evaluaciones.aplicacion.itemcuantitativoasesor.log.verificacion-registrar", 2),
    LOG_REGISTRADO("evaluaciones.aplicacion.itemcuantitativoasesor.log.registrado", 1),
    LOG_GUARDADO("evaluaciones.infraestructura.itemcuantitativoasesor.log.guardado", 1),
    LOG_CONSULTA_COMPLETADA(
            "evaluaciones.aplicacion.itemcuantitativoasesor.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    ItemCuantitativoAsesorKey(String clave, int parametros) {
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
