package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ProyectoGradoKey implements ClaveMensaje {

    LOG_PROYECTO_RECIBIDO("entregables.infraestructura.proyectogrado.log.proyecto-recibido", 3),
    LOG_VERIFICACION_AGREGAR("entregables.aplicacion.proyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADO("entregables.aplicacion.proyectogrado.log.agregado", 1),
    LOG_DUPLICADO("entregables.aplicacion.proyectogrado.log.duplicado", 1),
    LOG_GUARDADO("entregables.infraestructura.proyectogrado.log.guardado", 1),
    ERROR_FINALIZADO("entregables.dominio.proyectogrado.error.finalizado", 1),
    ERROR_FINALIZADO_DETALLE("entregables.dominio.proyectogrado.error.finalizado-detalle", 1),
    LOG_VERIFICACION_FINALIZAR("entregables.aplicacion.proyectogrado.log.verificacion-finalizar", 2),
    LOG_FINALIZACION_RECIBIDA("entregables.infraestructura.proyectogrado.log.finalizacion-recibida", 2),
    LOG_FINALIZADO("entregables.aplicacion.proyectogrado.log.finalizado", 1),
    LOG_YA_FINALIZADO("entregables.aplicacion.proyectogrado.log.ya-finalizado", 1),
    LOG_ESTADO_ACTUALIZADO("entregables.infraestructura.proyectogrado.log.estado-actualizado", 2),
    ERROR_NO_ENCONTRADO("entregables.dominio.proyectogrado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("entregables.dominio.proyectogrado.error.no-encontrado-detalle", 1),
    ERROR_ESTUDIANTE_NO_PERTENECE("entregables.dominio.proyectogrado.error.estudiante-no-pertenece", 0),
    ERROR_ESTUDIANTE_NO_PERTENECE_DETALLE(
            "entregables.dominio.proyectogrado.error.estudiante-no-pertenece-detalle", 2),
    ERROR_CANCELADO("entregables.dominio.proyectogrado.error.cancelado", 1),
    ERROR_CANCELADO_DETALLE("entregables.dominio.proyectogrado.error.cancelado-detalle", 1),
    LOG_CANCELACION_RECIBIDA("entregables.infraestructura.proyectogrado.log.cancelacion-recibida", 2),
    LOG_VERIFICACION_CANCELAR("entregables.aplicacion.proyectogrado.log.verificacion-cancelar", 2),
    LOG_CANCELADO("entregables.aplicacion.proyectogrado.log.cancelado", 1),
    LOG_YA_CANCELADO("entregables.aplicacion.proyectogrado.log.ya-cancelado", 1);

    private final String clave;
    private final int parametros;

    ProyectoGradoKey(String clave, int parametros) {
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
