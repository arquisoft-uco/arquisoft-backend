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
    ERROR_ESTADO_NO_ENCONTRADO_DETALLE("mapas_ruta.dominio.proyectogrado.error.estado-no-encontrado-detalle", 1);

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
