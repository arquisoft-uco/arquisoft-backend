package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ItemCualitativoAsesorKey implements ClaveMensaje {

    ERROR_NOMBRE_DUPLICADO(
            "evaluaciones.dominio.itemcualitativoasesor.error.nombre-duplicado", 1),
    ERROR_NO_ENCONTRADO("evaluaciones.dominio.itemcualitativoasesor.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE(
            "evaluaciones.dominio.itemcualitativoasesor.error.no-encontrado-detalle", 1),
    LOG_MODIFICANDO("evaluaciones.aplicacion.itemcualitativoasesor.log.modificando", 1),
    LOG_VERIFICACION_MODIFICAR(
            "evaluaciones.aplicacion.itemcualitativoasesor.log.verificacion-modificar", 1),
    LOG_MODIFICADO("evaluaciones.aplicacion.itemcualitativoasesor.log.modificado", 1),
    LOG_DESCRIPCION_ACTUALIZADA(
            "evaluaciones.infraestructura.itemcualitativoasesor.log.descripcion-actualizada", 1),
    LOG_REGISTRANDO(
            "evaluaciones.aplicacion.itemcualitativoasesor.log.registrando", 1),
    LOG_VERIFICACION_REGISTRAR(
            "evaluaciones.aplicacion.itemcualitativoasesor.log.verificacion-registrar", 1),
    LOG_REGISTRADO(
            "evaluaciones.aplicacion.itemcualitativoasesor.log.registrado", 1),
    LOG_GUARDADA(
            "evaluaciones.infraestructura.itemcualitativoasesor.log.guardada", 1),
    LOG_CONSULTA_COMPLETADA(
            "evaluaciones.aplicacion.itemcualitativoasesor.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    ItemCualitativoAsesorKey(String clave, int parametros) {
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
