package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum TareaKey implements ClaveMensaje {

    ERROR_DUPLICADA("mapas_ruta.dominio.tarea.error.duplicada", 2),
    ERROR_ESTADO_ITEM_NO_ENCONTRADO("mapas_ruta.dominio.tarea.error.estado-item-no-encontrado", 1),
    ERROR_NO_ENCONTRADA("mapas_ruta.dominio.tarea.error.no-encontrada", 1),
    ERROR_TRANSICION_ESTADO_ITEM_NO_PERMITIDA("mapas_ruta.dominio.tarea.error.transicion-estado-item-no-permitida", 3),
    LOG_CAMBIANDO_ESTADO_ITEM("mapas_ruta.aplicacion.tarea.log.cambiando-estado-item", 2),
    LOG_VERIFICACION_CAMBIAR_ESTADO_ITEM("mapas_ruta.aplicacion.tarea.log.verificacion-cambiar-estado-item", 1),
    LOG_ESTADO_ITEM_CAMBIADO("mapas_ruta.aplicacion.tarea.log.estado-item-cambiado", 3),
    LOG_ASIGNANDO("mapas_ruta.aplicacion.tarea.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("mapas_ruta.aplicacion.tarea.log.verificacion-asignar", 4),
    LOG_ASIGNADA("mapas_ruta.aplicacion.tarea.log.asignada", 2),
    LOG_GUARDADA("mapas_ruta.infraestructura.tarea.log.guardada", 1),
    LOG_CONSULTANDO_INFORMACION("mapas_ruta.aplicacion.tarea.log.consultando-informacion", 1),
    LOG_CONSULTA_INFORMACION_COMPLETADA("mapas_ruta.aplicacion.tarea.log.consulta-informacion-completada", 1),
    LOG_CONSULTANDO_POR_MAPA_RUTA("mapas_ruta.aplicacion.tarea.log.consultando-por-mapa-ruta", 5),
    LOG_CONSULTA_POR_MAPA_RUTA_COMPLETADA("mapas_ruta.aplicacion.tarea.log.consulta-por-mapa-ruta-completada", 3);

    private final String clave;
    private final int parametros;

    TareaKey(String clave, int parametros) {
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
