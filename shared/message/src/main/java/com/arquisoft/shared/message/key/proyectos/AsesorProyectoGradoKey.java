package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AsesorProyectoGrado. */
public enum AsesorProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNANDO("proyectos.aplicacion.asesorproyectogrado.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("proyectos.aplicacion.asesorproyectogrado.log.verificacion-asignar", 4),
    LOG_ASIGNADO("proyectos.aplicacion.asesorproyectogrado.log.asignado", 2),
    LOG_CONSULTANDO("proyectos.aplicacion.asesorproyectogrado.log.consultando", 2),
    LOG_CONSULTA_COMPLETADA("proyectos.aplicacion.asesorproyectogrado.log.consulta-completada", 1),
    LOG_CAMBIANDO_ESTADO("proyectos.aplicacion.asesorproyectogrado.log.cambiando-estado", 3),
    LOG_VERIFICACION_CAMBIO_ESTADO("proyectos.aplicacion.asesorproyectogrado.log.verificacion-cambio-estado", 2),
    LOG_ESTADO_CAMBIADO("proyectos.aplicacion.asesorproyectogrado.log.estado-cambiado", 3),
    LOG_GUARDADO("proyectos.infraestructura.asesorproyectogrado.log.guardado", 2),
    LOG_ACTUALIZADO("proyectos.infraestructura.asesorproyectogrado.log.actualizado", 2),
    ERROR_ASESOR_NO_VIGENTE("proyectos.dominio.asesorproyectogrado.error.asesor-no-vigente", 1),
    ERROR_YA_ASIGNADO("proyectos.dominio.asesorproyectogrado.error.ya-asignado", 1),
    ERROR_CUPO_EXCEDIDO("proyectos.dominio.asesorproyectogrado.error.cupo-excedido", 1),
    ERROR_NO_ENCONTRADO("proyectos.dominio.asesorproyectogrado.error.no-encontrado", 2),
    ERROR_TRANSICION_INVALIDA("proyectos.dominio.asesorproyectogrado.error.transicion-invalida", 2);

    private final String clave;
    private final int parametros;

    AsesorProyectoGradoKey(String clave, int parametros) {
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
