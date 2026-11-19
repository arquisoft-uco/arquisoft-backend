package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ProyectoGradoMapasRutaKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("mapas_ruta.dominio.proyectogrado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("mapas_ruta.dominio.proyectogrado.error.no-encontrado-detalle", 1),
    ERROR_NO_PROPIETARIO("mapas_ruta.dominio.proyectogrado.error.no-propietario", 1),
    ERROR_NO_PROPIETARIO_DETALLE("mapas_ruta.dominio.proyectogrado.error.no-propietario-detalle", 1),
    ERROR_NO_EN_PROCESO("mapas_ruta.dominio.proyectogrado.error.no-en-proceso", 2),
    ERROR_NO_EN_PROCESO_DETALLE("mapas_ruta.dominio.proyectogrado.error.no-en-proceso-detalle", 2),
    ERROR_ESTADO_NO_ENCONTRADO("mapas_ruta.dominio.proyectogrado.error.estado-no-encontrado", 0),
    ERROR_ESTADO_NO_ENCONTRADO_DETALLE("mapas_ruta.dominio.proyectogrado.error.estado-no-encontrado-detalle", 1),
    LOG_PROYECTO_RECIBIDO("mapas_ruta.infraestructura.proyectogrado.log.proyecto-recibido", 3),
    LOG_VERIFICACION_AGREGAR("mapas_ruta.aplicacion.proyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADO("mapas_ruta.infraestructura.proyectogrado.log.agregado", 1),
    LOG_DUPLICADO("mapas_ruta.infraestructura.proyectogrado.log.duplicado", 1),
    LOG_GUARDADO("mapas_ruta.infraestructura.proyectogrado.log.guardado", 1),
    LOG_ESTADO_RECIBIDO("mapas_ruta.infraestructura.proyectogrado.log.estado-recibido", 4),
    LOG_VERIFICACION_ACTUALIZAR_ESTADO(
            "mapas_ruta.aplicacion.proyectogrado.log.verificacion-actualizar-estado", 2),
    LOG_ESTADO_APLICADO("mapas_ruta.infraestructura.proyectogrado.log.estado-aplicado", 2),
    LOG_ESTADO_DESCARTADO("mapas_ruta.infraestructura.proyectogrado.log.estado-descartado", 2),
    LOG_ESTADO_ACTUALIZADO("mapas_ruta.infraestructura.proyectogrado.log.estado-actualizado", 3);

    private final String clave;
    private final int parametros;

    ProyectoGradoMapasRutaKey(String clave, int parametros) {
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
