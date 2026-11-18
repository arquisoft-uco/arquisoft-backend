package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ItemCuantitativoAsesorKey implements ClaveMensaje {

    ERROR_CATEGORIA_NO_ENCONTRADA(
            "evaluaciones.dominio.itemcuantitativoasesor.error.categoria-no-encontrada", 0),
    ERROR_CATEGORIA_NO_ENCONTRADA_DETALLE(
            "evaluaciones.dominio.itemcuantitativoasesor.error.categoria-no-encontrada-detalle", 1),
    ERROR_NOMBRE_CATEGORIA_DUPLICADO(
            "evaluaciones.dominio.itemcuantitativoasesor.error.nombre-categoria-duplicado", 1),
    ERROR_NOMBRE_CATEGORIA_DUPLICADO_DETALLE(
            "evaluaciones.dominio.itemcuantitativoasesor.error.nombre-categoria-duplicado-detalle", 2),
    ERROR_NO_ENCONTRADO("evaluaciones.dominio.itemcuantitativoasesor.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE(
            "evaluaciones.dominio.itemcuantitativoasesor.error.no-encontrado-detalle", 1),
    ERROR_EN_USO("evaluaciones.dominio.itemcuantitativoasesor.error.en-uso", 0),
    ERROR_EN_USO_DETALLE("evaluaciones.dominio.itemcuantitativoasesor.error.en-uso-detalle", 1),
    LOG_REMOVIENDO("evaluaciones.aplicacion.itemcuantitativoasesor.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER(
            "evaluaciones.aplicacion.itemcuantitativoasesor.log.verificacion-remover", 2),
    LOG_REMOVIDO("evaluaciones.aplicacion.itemcuantitativoasesor.log.removido", 1),
    LOG_ELIMINADO("evaluaciones.infraestructura.itemcuantitativoasesor.log.eliminado", 1),
    LOG_MODIFICANDO("evaluaciones.aplicacion.itemcuantitativoasesor.log.modificando", 1),
    LOG_VERIFICACION_MODIFICAR(
            "evaluaciones.aplicacion.itemcuantitativoasesor.log.verificacion-modificar", 1),
    LOG_MODIFICADO("evaluaciones.aplicacion.itemcuantitativoasesor.log.modificado", 1),
    LOG_DESCRIPCION_ACTUALIZADA(
            "evaluaciones.infraestructura.itemcuantitativoasesor.log.descripcion-actualizada", 1),
    LOG_REGISTRANDO("evaluaciones.aplicacion.itemcuantitativoasesor.log.registrando", 2),
    LOG_VERIFICACION_REGISTRAR(
            "evaluaciones.aplicacion.itemcuantitativoasesor.log.verificacion-registrar", 2),
    LOG_REGISTRADO("evaluaciones.aplicacion.itemcuantitativoasesor.log.registrado", 1),
    LOG_GUARDADO("evaluaciones.infraestructura.itemcuantitativoasesor.log.guardado", 1),
    LOG_CONSULTA_COMPLETADA(
            "evaluaciones.aplicacion.itemcuantitativoasesor.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    ItemCuantitativoAsesorKey(String clave, int parametros) {
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
