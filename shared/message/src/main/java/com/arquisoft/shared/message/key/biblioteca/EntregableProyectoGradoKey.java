package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EntregableProyectoGradoKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("biblioteca.dominio.entregableproyectogrado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("biblioteca.dominio.entregableproyectogrado.error.no-encontrado-detalle", 1),
    ERROR_ARCHIVO_NO_DISPONIBLE("biblioteca.dominio.entregableproyectogrado.error.archivo-no-disponible", 0),
    ERROR_ARCHIVO_NO_DISPONIBLE_DETALLE(
            "biblioteca.dominio.entregableproyectogrado.error.archivo-no-disponible-detalle", 1),
    LOG_VERIFICACION_AGREGAR("biblioteca.aplicacion.entregableproyectogrado.log.verificacion-agregar", 2),
    LOG_GUARDADO("biblioteca.infraestructura.entregableproyectogrado.log.guardado", 1),
    LOG_APROBADO_RECIBIDO("biblioteca.infraestructura.entregableproyectogrado.log.aprobado-recibido", 3),
    LOG_AGREGADO("biblioteca.infraestructura.entregableproyectogrado.log.agregado", 2),
    LOG_DESCARTADO("biblioteca.infraestructura.entregableproyectogrado.log.descartado", 1),
    LOG_CONSULTANDO_DISPONIBLES(
            "biblioteca.aplicacion.entregableproyectogrado.log.consultando-disponibles", 3),
    LOG_CONSULTA_DISPONIBLES_COMPLETADA(
            "biblioteca.aplicacion.entregableproyectogrado.log.consulta-disponibles-completada", 3);

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
