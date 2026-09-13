package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AgregarAsesorFicha. */
public enum AgregarAsesorFichaKey implements ClaveMensaje {

    ERROR_USUARIO_DUPLICADO("usuarios.dominio.asesorficha.error.usuario-duplicado", 1),
    LOG_VERIFICACION_AGREGAR("usuarios.aplicacion.asesorficha.log.verificacion-agregar", 2),
    LOG_GUARDADO("usuarios.infraestructura.asesorficha.log.guardado", 1);

    private final String clave;
    private final int parametros;

    AgregarAsesorFichaKey(String clave, int parametros) {
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
