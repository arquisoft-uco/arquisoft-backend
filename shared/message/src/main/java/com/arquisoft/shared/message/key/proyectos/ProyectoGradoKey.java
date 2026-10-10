package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ProyectoGrado. */
public enum ProyectoGradoKey implements ClaveMensaje {

    LOG_FICHA_APROBADA_RECIBIDA("proyectos.infraestructura.proyectogrado.log.ficha-aprobada-recibida", 3),
    LOG_REGISTRANDO("proyectos.aplicacion.proyectogrado.log.registrando", 2),
    LOG_VERIFICACION_PREVIA("proyectos.aplicacion.proyectogrado.log.verificacion-previa", 2),
    LOG_REGISTRADO_CIERRE("proyectos.aplicacion.proyectogrado.log.registrado-cierre", 1),
    LOG_REGISTRADO("proyectos.infraestructura.proyectogrado.log.registrado", 2),
    LOG_DUPLICADO("proyectos.infraestructura.proyectogrado.log.duplicado", 1),
    LOG_GUARDADO("proyectos.infraestructura.proyectogrado.log.guardado", 2),
    ERROR_COORDINADOR_NO_VIGENTE("proyectos.dominio.proyectogrado.error.coordinador-no-vigente", 1),
    ERROR_NO_ENCONTRADO("proyectos.dominio.proyectogrado.error.no-encontrado", 1),
    ERROR_FINALIZADO("proyectos.dominio.proyectogrado.error.finalizado", 1);

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
