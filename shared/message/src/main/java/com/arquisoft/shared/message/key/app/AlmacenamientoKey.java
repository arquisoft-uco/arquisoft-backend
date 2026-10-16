package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

public enum AlmacenamientoKey implements ClaveMensaje {

    ERROR_FORMULARIO_CARGA("app.infraestructura.almacenamiento.error.formulario-carga", 0),
    ERROR_CONSULTA("app.infraestructura.almacenamiento.error.consulta", 0),
    ERROR_LECTURA("app.infraestructura.almacenamiento.error.lectura", 0),
    ERROR_COPIA("app.infraestructura.almacenamiento.error.copia", 0),
    ERROR_ELIMINACION("app.infraestructura.almacenamiento.error.eliminacion", 0);

    private final String clave;
    private final int parametros;

    AlmacenamientoKey(String clave, int parametros) {
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
