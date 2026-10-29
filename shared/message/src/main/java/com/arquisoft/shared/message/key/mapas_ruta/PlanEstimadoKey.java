package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum PlanEstimadoKey implements ClaveMensaje {

    ERROR_DUPLICADO("mapas_ruta.dominio.planestimado.error.duplicado", 1),
    ERROR_DUPLICADO_DETALLE("mapas_ruta.dominio.planestimado.error.duplicado-detalle", 1),
    ERROR_NO_ENCONTRADO("mapas_ruta.dominio.planestimado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("mapas_ruta.dominio.planestimado.error.no-encontrado-detalle", 1),
    ERROR_TIEMPO_EXCEDE_PLAZO("mapas_ruta.dominio.planestimado.error.tiempo-excede-plazo", 2),
    ERROR_TIEMPO_EXCEDE_PLAZO_DETALLE("mapas_ruta.dominio.planestimado.error.tiempo-excede-plazo-detalle", 4),
    LOG_AGREGANDO("mapas_ruta.aplicacion.planestimado.log.agregando", 2),
    LOG_VERIFICACION_AGREGAR("mapas_ruta.aplicacion.planestimado.log.verificacion-agregar", 4),
    LOG_AGREGADO("mapas_ruta.aplicacion.planestimado.log.agregado", 2),
    LOG_CONSULTANDO("mapas_ruta.aplicacion.planestimado.log.consultando", 5),
    LOG_CONSULTA_COMPLETADA("mapas_ruta.aplicacion.planestimado.log.consulta-completada", 3),
    LOG_MODIFICANDO("mapas_ruta.aplicacion.planestimado.log.modificando", 1),
    LOG_VERIFICACION_MODIFICAR("mapas_ruta.aplicacion.planestimado.log.verificacion-modificar", 1),
    LOG_MODIFICADO("mapas_ruta.aplicacion.planestimado.log.modificado", 1),
    LOG_GUARDADO("mapas_ruta.infraestructura.planestimado.log.guardado", 1);

    private final String clave;
    private final int parametros;

    PlanEstimadoKey(String clave, int parametros) {
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
