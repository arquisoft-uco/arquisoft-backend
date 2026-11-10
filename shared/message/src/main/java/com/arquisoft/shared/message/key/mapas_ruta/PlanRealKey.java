package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum PlanRealKey implements ClaveMensaje {

    LOG_VERIFICACION_GENERAR("mapas_ruta.aplicacion.planreal.log.verificacion-generar", 2),
    LOG_TAREA_FINALIZADA_RECIBIDA("mapas_ruta.aplicacion.planreal.log.tarea-finalizada-recibida", 2),
    LOG_GENERADO("mapas_ruta.aplicacion.planreal.log.generado", 2),
    LOG_RECALCULADO("mapas_ruta.aplicacion.planreal.log.recalculado", 2),
    LOG_DESCARTADO("mapas_ruta.aplicacion.planreal.log.descartado", 1),
    LOG_GUARDADO("mapas_ruta.infraestructura.planreal.log.guardado", 1),
    LOG_ACTUALIZADO("mapas_ruta.infraestructura.planreal.log.actualizado", 1),
    ERROR_NO_ENCONTRADO("mapas_ruta.dominio.planreal.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("mapas_ruta.dominio.planreal.error.no-encontrado-detalle", 1),
    LOG_MODIFICANDO("mapas_ruta.aplicacion.planreal.log.modificando", 2),
    LOG_VERIFICACION_MODIFICAR("mapas_ruta.aplicacion.planreal.log.verificacion-modificar", 1),
    LOG_MODIFICADO("mapas_ruta.aplicacion.planreal.log.modificado", 1),
    LOG_CONSULTANDO("mapas_ruta.aplicacion.planreal.log.consultando", 5),
    LOG_CONSULTA_COMPLETADA("mapas_ruta.aplicacion.planreal.log.consulta-completada", 3),
    LOG_CONSULTANDO_POR_PROYECTO_GRADO("mapas_ruta.aplicacion.planreal.log.consultando-por-proyecto-grado", 5),
    LOG_CONSULTA_POR_PROYECTO_GRADO_COMPLETADA(
            "mapas_ruta.aplicacion.planreal.log.consulta-por-proyecto-grado-completada", 3);

    private final String clave;
    private final int parametros;

    PlanRealKey(String clave, int parametros) {
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
