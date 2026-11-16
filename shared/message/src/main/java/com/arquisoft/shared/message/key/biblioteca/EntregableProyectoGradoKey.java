package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EntregableProyectoGradoKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("biblioteca.dominio.entregableproyectogrado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("biblioteca.dominio.entregableproyectogrado.error.no-encontrado-detalle", 1),
    ERROR_ARCHIVO_NO_DISPONIBLE("biblioteca.dominio.entregableproyectogrado.error.archivo-no-disponible", 0),
    ERROR_ARCHIVO_NO_DISPONIBLE_DETALLE(
            "biblioteca.dominio.entregableproyectogrado.error.archivo-no-disponible-detalle", 1);

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
