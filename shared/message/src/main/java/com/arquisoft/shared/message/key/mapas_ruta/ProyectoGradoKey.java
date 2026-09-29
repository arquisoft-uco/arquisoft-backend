package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ProyectoGradoKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("mapas_ruta.dominio.proyectogrado.error.no-encontrado", 1),
    ERROR_NO_PROPIETARIO("mapas_ruta.dominio.proyectogrado.error.no-propietario", 1),
    ERROR_NO_EN_PROCESO("mapas_ruta.dominio.proyectogrado.error.no-en-proceso", 2),
    ERROR_ESTADO_NO_ENCONTRADO("mapas_ruta.dominio.proyectogrado.error.estado-no-encontrado", 1);

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
