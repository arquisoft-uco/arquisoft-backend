package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ItemCuantitativoJuradoKey implements ClaveMensaje {

    ERROR_CATEGORIA_NO_ENCONTRADA(
            "evaluaciones.dominio.itemcuantitativojurado.error.categoria-no-encontrada", 1),
    ERROR_NOMBRE_CATEGORIA_DUPLICADO(
            "evaluaciones.dominio.itemcuantitativojurado.error.nombre-categoria-duplicado", 2),
    ERROR_NO_ENCONTRADO(
            "evaluaciones.dominio.itemcuantitativojurado.error.no-encontrado", 1),
    ERROR_EN_USO(
            "evaluaciones.dominio.itemcuantitativojurado.error.en-uso", 1),
    LOG_REGISTRANDO("evaluaciones.aplicacion.itemcuantitativojurado.log.registrando", 2),
    LOG_VERIFICACION_REGISTRAR(
            "evaluaciones.aplicacion.itemcuantitativojurado.log.verificacion-registrar", 2),
    LOG_REGISTRADO("evaluaciones.aplicacion.itemcuantitativojurado.log.registrado", 1),
    LOG_MODIFICANDO("evaluaciones.aplicacion.itemcuantitativojurado.log.modificando", 1),
    LOG_VERIFICACION_MODIFICAR(
            "evaluaciones.aplicacion.itemcuantitativojurado.log.verificacion-modificar", 1),
    LOG_MODIFICADO("evaluaciones.aplicacion.itemcuantitativojurado.log.modificado", 1),
    LOG_REMOVIENDO("evaluaciones.aplicacion.itemcuantitativojurado.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER(
            "evaluaciones.aplicacion.itemcuantitativojurado.log.verificacion-remover", 2),
    LOG_REMOVIDO("evaluaciones.aplicacion.itemcuantitativojurado.log.removido", 1),
    LOG_GUARDADO("evaluaciones.infraestructura.itemcuantitativojurado.log.guardado", 1),
    LOG_DESCRIPCION_ACTUALIZADA(
            "evaluaciones.infraestructura.itemcuantitativojurado.log.descripcion-actualizada", 1),
    LOG_ELIMINADO("evaluaciones.infraestructura.itemcuantitativojurado.log.eliminado", 1),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.itemcuantitativojurado.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    ItemCuantitativoJuradoKey(String clave, int parametros) {
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
