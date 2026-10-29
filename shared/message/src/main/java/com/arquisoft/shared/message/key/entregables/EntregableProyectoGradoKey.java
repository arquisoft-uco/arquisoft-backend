package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EntregableProyectoGradoKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("entregables.dominio.entregableproyectogrado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("entregables.dominio.entregableproyectogrado.error.no-encontrado-detalle", 1),
    ERROR_ESTADO_INVALIDO("entregables.dominio.entregableproyectogrado.error.estado-invalido", 0),
    ERROR_ESTADO_DESTINO_NO_PERMITIDO(
            "entregables.dominio.entregableproyectogrado.error.estado-destino-no-permitido", 1),
    ERROR_ESTADO_DESTINO_NO_PERMITIDO_DETALLE(
            "entregables.dominio.entregableproyectogrado.error.estado-destino-no-permitido-detalle", 2),
    ERROR_CIERRE_NO_PERMITIDO("entregables.dominio.entregableproyectogrado.error.cierre-no-permitido", 1),
    ERROR_CIERRE_NO_PERMITIDO_DETALLE(
            "entregables.dominio.entregableproyectogrado.error.cierre-no-permitido-detalle", 2),
    LOG_CAMBIANDO_ESTADO("entregables.aplicacion.entregableproyectogrado.log.cambiando-estado", 2),
    LOG_VERIFICACION_CAMBIAR_ESTADO(
            "entregables.aplicacion.entregableproyectogrado.log.verificacion-cambiar-estado", 2),
    LOG_ESTADO_CAMBIADO("entregables.aplicacion.entregableproyectogrado.log.estado-cambiado", 3),
    LOG_ESTADO_ACTUALIZADO("entregables.infraestructura.entregableproyectogrado.log.estado-actualizado", 1),
    LOG_GENERANDO("entregables.aplicacion.entregableproyectogrado.log.generando", 2),
    LOG_VERIFICACION_GENERAR("entregables.aplicacion.entregableproyectogrado.log.verificacion-generar", 3),
    LOG_GENERADO("entregables.aplicacion.entregableproyectogrado.log.generado", 3),
    LOG_GUARDADO("entregables.infraestructura.entregableproyectogrado.log.guardado", 1),
    LOG_CONSULTANDO("entregables.aplicacion.entregableproyectogrado.log.consultando", 4),
    LOG_CONSULTA_COMPLETADA("entregables.aplicacion.entregableproyectogrado.log.consulta-completada", 3),
    LOG_CONSULTANDO_ESTUDIANTE("entregables.aplicacion.entregableproyectogrado.log.consultando-estudiante", 5);

    private final String clave;
    private final int parametros;

    EntregableProyectoGradoKey(String clave, int parametros) {
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
