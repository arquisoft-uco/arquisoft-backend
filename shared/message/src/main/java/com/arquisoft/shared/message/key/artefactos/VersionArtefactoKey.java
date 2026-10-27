package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum VersionArtefactoKey implements ClaveMensaje {

    ERROR_CARGA_NO_ENCONTRADA("artefactos.dominio.versionartefacto.error.carga-no-encontrada", 0),
    ERROR_CARGA_NO_ENCONTRADA_DETALLE("artefactos.dominio.versionartefacto.error.carga-no-encontrada-detalle", 1),
    ERROR_CARGA_NO_CORRESPONDE("artefactos.dominio.versionartefacto.error.carga-no-corresponde", 1),
    ERROR_CARGA_NO_CORRESPONDE_DETALLE("artefactos.dominio.versionartefacto.error.carga-no-corresponde-detalle", 1),
    ERROR_CARGA_MODIFICADA("artefactos.dominio.versionartefacto.error.carga-modificada", 1),
    ERROR_CARGA_MODIFICADA_DETALLE("artefactos.dominio.versionartefacto.error.carga-modificada-detalle", 1),
    ERROR_TAMANO_EXCEDIDO("artefactos.dominio.versionartefacto.error.tamano-excedido", 2),
    ERROR_TAMANO_EXCEDIDO_DETALLE("artefactos.dominio.versionartefacto.error.tamano-excedido-detalle", 2),
    ERROR_TIPO_NO_COINCIDE("artefactos.dominio.versionartefacto.error.tipo-no-coincide", 1),
    ERROR_TIPO_NO_COINCIDE_DETALLE("artefactos.dominio.versionartefacto.error.tipo-no-coincide-detalle", 2),
    ERROR_NOMBRE_ARCHIVO_INVALIDO("artefactos.dominio.versionartefacto.error.nombre-archivo-invalido", 1),
    ERROR_TIPO_NO_PERMITIDO("artefactos.dominio.versionartefacto.error.tipo-no-permitido", 1),
    ERROR_TIPO_CONTENIDO_NO_COINCIDE_EXTENSION(
            "artefactos.dominio.versionartefacto.error.tipo-contenido-no-coincide-extension", 1),
    LOG_SOLICITANDO_CARGA("artefactos.aplicacion.versionartefacto.log.solicitando-carga", 2),
    LOG_VERIFICACION_SOLICITAR_CARGA("artefactos.aplicacion.versionartefacto.log.verificacion-solicitar-carga", 2),
    LOG_CARGA_SOLICITADA("artefactos.aplicacion.versionartefacto.log.carga-solicitada", 2),
    LOG_GENERANDO("artefactos.aplicacion.versionartefacto.log.generando", 3),
    LOG_VERIFICACION_GENERAR("artefactos.aplicacion.versionartefacto.log.verificacion-generar", 5),
    LOG_GENERADA("artefactos.aplicacion.versionartefacto.log.generada", 3),
    LOG_CONSULTANDO_ASESOR("artefactos.aplicacion.versionartefacto.log.consultando-asesor", 4),
    LOG_CONSULTA_ASESOR_COMPLETADA("artefactos.aplicacion.versionartefacto.log.consulta-asesor-completada", 1),
    LOG_GUARDADA("artefactos.infraestructura.versionartefacto.log.guardada", 1),
    LOG_CARGA_COPIADA("artefactos.infraestructura.versionartefacto.log.carga-copiada", 2),
    LOG_CARGA_ELIMINADA("artefactos.infraestructura.versionartefacto.log.carga-eliminada", 1);

    private final String clave;
    private final int parametros;

    VersionArtefactoKey(String clave, int parametros) {
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
