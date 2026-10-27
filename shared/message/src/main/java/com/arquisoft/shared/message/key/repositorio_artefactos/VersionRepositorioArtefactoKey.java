package com.arquisoft.shared.message.key.repositorio_artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de VersionRepositorioArtefacto (repositorio_artefactos). */
public enum VersionRepositorioArtefactoKey implements ClaveMensaje {

    ERROR_CARGA_NO_ENCONTRADA(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.carga-no-encontrada", 1),
    ERROR_CARGA_NO_CORRESPONDE(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.carga-no-corresponde", 1),
    ERROR_CARGA_MODIFICADA(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.carga-modificada", 1),
    ERROR_TAMANO_EXCEDIDO(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.tamano-excedido", 2),
    ERROR_TIPO_NO_COINCIDE(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.tipo-no-coincide", 2),
    ERROR_DOCUMENTO_YA_EXISTE(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.documento-ya-existe", 1),
    ERROR_DOCUMENTO_NO_EXISTE(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.documento-no-existe", 1),
    ERROR_NOMBRE_ARCHIVO_INVALIDO(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.nombre-archivo-invalido", 1),
    ERROR_TIPO_NO_PERMITIDO(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.tipo-no-permitido", 1),
    ERROR_TIPO_CONTENIDO_NO_COINCIDE_EXTENSION(
            "repositorio_artefactos.dominio.versionrepositorioartefacto.error.tipo-contenido-no-coincide-extension", 2),
    LOG_SOLICITANDO_CARGA(
            "repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.solicitando-carga", 2),
    LOG_VERIFICACION_SOLICITAR_CARGA(
            "repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.verificacion-solicitar-carga", 2),
    LOG_CARGA_SOLICITADA(
            "repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.carga-solicitada", 2),
    LOG_SUBIENDO("repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.subiendo", 3),
    LOG_VERIFICACION_SUBIR(
            "repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.verificacion-subir", 5),
    LOG_SUBIDA("repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.subida", 3),
    LOG_CONSULTANDO("repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA(
            "repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.consulta-completada", 1),
    LOG_CONSULTANDO_ASESOR(
            "repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.consultando-asesor", 4),
    LOG_CONSULTA_ASESOR_COMPLETADA(
            "repositorio_artefactos.aplicacion.versionrepositorioartefacto.log.consulta-asesor-completada", 3),
    LOG_GUARDADA("repositorio_artefactos.infraestructura.versionrepositorioartefacto.log.guardada", 1),
    LOG_CARGA_COPIADA("repositorio_artefactos.infraestructura.versionrepositorioartefacto.log.carga-copiada", 2),
    LOG_CARGA_ELIMINADA("repositorio_artefactos.infraestructura.versionrepositorioartefacto.log.carga-eliminada", 1);

    private final String clave;
    private final int parametros;

    VersionRepositorioArtefactoKey(String clave, int parametros) {
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
