package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ProyectoGrado (réplica local en artefactos). */
public enum ProyectoGradoKey implements ClaveMensaje {

    LOG_PROYECTO_RECIBIDO("artefactos.infraestructura.proyectogrado.log.proyecto-recibido", 3),
    LOG_VERIFICACION_AGREGAR("artefactos.aplicacion.proyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADO("artefactos.aplicacion.proyectogrado.log.agregado", 1),
    LOG_DUPLICADO("artefactos.aplicacion.proyectogrado.log.duplicado", 1),
    LOG_GUARDADO("artefactos.infraestructura.proyectogrado.log.guardado", 1),
    ERROR_NO_ENCONTRADO("artefactos.dominio.proyectogrado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("artefactos.dominio.proyectogrado.error.no-encontrado-detalle", 1),
    LOG_VERIFICACION_CONGELAR("artefactos.aplicacion.proyectogrado.log.verificacion-congelar", 2),
    LOG_CONGELADO("artefactos.aplicacion.proyectogrado.log.congelado", 1),
    LOG_YA_CONGELADO("artefactos.aplicacion.proyectogrado.log.ya-congelado", 1),
    LOG_FINALIZACION_RECIBIDA("artefactos.infraestructura.proyectogrado.log.finalizacion-recibida", 3),
    LOG_VERIFICACION_FINALIZAR("artefactos.aplicacion.proyectogrado.log.verificacion-finalizar", 2),
    LOG_FINALIZADO("artefactos.aplicacion.proyectogrado.log.finalizado", 1),
    LOG_YA_FINALIZADO("artefactos.aplicacion.proyectogrado.log.ya-finalizado", 1),
    LOG_VERIFICACION_CONGELAMIENTO("artefactos.aplicacion.proyectogrado.log.verificacion-congelamiento", 2),
    LOG_ENTREGADO_MARCADO("artefactos.infraestructura.proyectogrado.log.entregado-marcado", 2),
    LOG_FINALIZADO_MARCADO("artefactos.infraestructura.proyectogrado.log.finalizado-marcado", 2),
    LOG_CANCELACION_RECIBIDA("artefactos.infraestructura.proyectogrado.log.cancelacion-recibida", 4),
    LOG_VERIFICACION_DESCONGELAR("artefactos.aplicacion.proyectogrado.log.verificacion-descongelar", 2),
    LOG_DESCONGELADO("artefactos.aplicacion.proyectogrado.log.descongelado", 1),
    LOG_OMITIDO_DESCONGELAR("artefactos.aplicacion.proyectogrado.log.omitido-descongelar", 1),
    LOG_ENTREGADO_LIMPIADO("artefactos.infraestructura.proyectogrado.log.entregado-limpiado", 2),
    ERROR_CONGELADO("artefactos.dominio.proyectogrado.error.congelado", 1),
    ERROR_CONGELADO_DETALLE("artefactos.dominio.proyectogrado.error.congelado-detalle", 1),
    ERROR_NO_FINALIZADO("artefactos.dominio.proyectogrado.error.no-finalizado", 1),
    ERROR_NO_FINALIZADO_DETALLE("artefactos.dominio.proyectogrado.error.no-finalizado-detalle", 1);

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
