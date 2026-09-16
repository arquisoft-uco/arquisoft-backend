package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstudiantesProyectoGradoKey implements ClaveMensaje {

    LOG_ESTUDIANTES_NO_VERIFICADOS(
            "evaluaciones.infraestructura.estudiantesproyectogrado.log.estudiantes-no-verificados", 1);

    private final String clave;
    private final int parametros;

    EstudiantesProyectoGradoKey(String clave, int parametros) {
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
