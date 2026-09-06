package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ItemCualitativoJuradoKey implements ClaveMensaje {

    ERROR_NOMBRE_DUPLICADO("evaluaciones.dominio.itemcualitativojurado.error.nombre-duplicado", 1),
    ERROR_NO_ENCONTRADO("evaluaciones.dominio.itemcualitativojurado.error.no-encontrado", 1),
    LOG_REGISTRANDO("evaluaciones.aplicacion.itemcualitativojurado.log.registrando", 1),
    LOG_VERIFICACION_REGISTRAR("evaluaciones.aplicacion.itemcualitativojurado.log.verificacion-registrar", 1),
    LOG_REGISTRADO("evaluaciones.aplicacion.itemcualitativojurado.log.registrado", 1),
    LOG_GUARDADA("evaluaciones.infraestructura.itemcualitativojurado.log.guardada", 1),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.itemcualitativojurado.log.consulta-completada", 1);
    LOG_MODIFICANDO("evaluaciones.aplicacion.itemcualitativojurado.log.modificando", 1),
    LOG_VERIFICACION_MODIFICAR("evaluaciones.aplicacion.itemcualitativojurado.log.verificacion-modificar", 1),
    LOG_MODIFICADO("evaluaciones.aplicacion.itemcualitativojurado.log.modificado", 1),
    LOG_GUARDADA("evaluaciones.infraestructura.itemcualitativojurado.log.guardada", 1),
    LOG_DESCRIPCION_ACTUALIZADA("evaluaciones.infraestructura.itemcualitativojurado.log.descripcion-actualizada", 1);

    private final String clave;
    private final int parametros;

    ItemCualitativoJuradoKey(String clave, int parametros) {
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
