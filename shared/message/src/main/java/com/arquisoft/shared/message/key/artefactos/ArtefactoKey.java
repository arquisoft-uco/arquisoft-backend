package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de la asignación de artefactos a un proyecto de grado. */
public enum ArtefactoKey implements ClaveMensaje {

    LOG_ASIGNANDO("artefactos.aplicacion.artefacto.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("artefactos.aplicacion.artefacto.log.verificacion-asignar", 1),
    LOG_VERIFICACION_REGISTRAR("artefactos.aplicacion.artefacto.log.verificacion-registrar", 5),
    LOG_VERIFICACION_REACTIVAR("artefactos.aplicacion.artefacto.log.verificacion-reactivar", 4),
    LOG_ASIGNADO("artefactos.aplicacion.artefacto.log.asignado", 2),
    LOG_TRASPASANDO("artefactos.aplicacion.artefacto.log.traspasando", 2),
    LOG_CONSULTANDO_ASESOR("artefactos.aplicacion.artefacto.log.consultando-asesor", 4),
    LOG_CONSULTA_ASESOR_COMPLETADA("artefactos.aplicacion.artefacto.log.consulta-asesor-completada", 1),
    LOG_CONSULTANDO_SIN_AVANCE("artefactos.aplicacion.artefacto.log.consultando-sin-avance", 4),
    LOG_CONSULTA_SIN_AVANCE_COMPLETADA("artefactos.aplicacion.artefacto.log.consulta-sin-avance-completada", 1),
    LOG_CONSULTANDO_ESTUDIANTE("artefactos.aplicacion.artefacto.log.consultando-estudiante", 4),
    LOG_CONSULTA_ESTUDIANTE_COMPLETADA("artefactos.aplicacion.artefacto.log.consulta-estudiante-completada", 1),
    LOG_CAMBIANDO_ASESOR("artefactos.aplicacion.artefacto.log.cambiando-asesor", 2),
    LOG_VERIFICACION_CAMBIAR_ASESOR("artefactos.aplicacion.artefacto.log.verificacion-cambiar-asesor", 3),
    LOG_ASESOR_CAMBIADO("artefactos.aplicacion.artefacto.log.asesor-cambiado", 2),
    LOG_ASESOR_ACTUALIZADO("artefactos.infraestructura.artefacto.log.asesor-actualizado", 1),
    LOG_OMITIENDO("artefactos.aplicacion.artefacto.log.omitiendo", 2),
    LOG_VERIFICACION_OMITIR("artefactos.aplicacion.artefacto.log.verificacion-omitir", 4),
    LOG_OMITIDO("artefactos.aplicacion.artefacto.log.omitido", 2),
    LOG_OMITIDO_ACTUALIZADO("artefactos.infraestructura.artefacto.log.omitido-actualizado", 1),
    LOG_CAMBIANDO_VERSION_REPOSITORIO("artefactos.aplicacion.artefacto.log.cambiando-version-repositorio", 2),
    LOG_VERIFICACION_CAMBIAR_VERSION_REPOSITORIO(
            "artefactos.aplicacion.artefacto.log.verificacion-cambiar-version-repositorio", 7),
    LOG_VERSION_REPOSITORIO_CAMBIADA("artefactos.aplicacion.artefacto.log.version-repositorio-cambiada", 3),
    LOG_VERSION_REPOSITORIO_ACTUALIZADA(
            "artefactos.infraestructura.artefacto.log.version-repositorio-actualizada", 1),
    ERROR_CAMBIO_VERSION_REPOSITORIO_NO_PERMITIDO(
            "artefactos.dominio.artefacto.error.cambio-version-repositorio-no-permitido", 1),
    ERROR_CAMBIO_VERSION_REPOSITORIO_NO_PERMITIDO_DETALLE(
            "artefactos.dominio.artefacto.error.cambio-version-repositorio-no-permitido-detalle", 2),
    ERROR_VERSION_OTRO_DOCUMENTO("artefactos.dominio.artefacto.error.version-otro-documento", 2),
    ERROR_VERSION_OTRO_DOCUMENTO_DETALLE(
            "artefactos.dominio.artefacto.error.version-otro-documento-detalle", 2),
    ERROR_VERSION_IGUAL_A_ACTUAL("artefactos.dominio.artefacto.error.version-igual-a-actual", 2),
    ERROR_VERSION_IGUAL_A_ACTUAL_DETALLE("artefactos.dominio.artefacto.error.version-igual-a-actual-detalle", 1),
    LOG_REACTIVADO_ACTUALIZADO("artefactos.infraestructura.artefacto.log.reactivado-actualizado", 1),
    ERROR_OMITIDO("artefactos.dominio.artefacto.error.omitido", 0),
    ERROR_OMITIDO_DETALLE("artefactos.dominio.artefacto.error.omitido-detalle", 1),
    ERROR_NO_ENCONTRADO("artefactos.dominio.artefacto.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("artefactos.dominio.artefacto.error.no-encontrado-detalle", 1),
    LOG_GUARDADO("artefactos.infraestructura.artefacto.log.guardado", 1),
    LOG_TRASPASADOS("artefactos.infraestructura.artefacto.log.traspasados", 1),
    ERROR_REPOSITORIO_YA_ASIGNADO("artefactos.dominio.artefacto.error.repositorio-ya-asignado", 1),
    ERROR_REPOSITORIO_YA_ASIGNADO_DETALLE("artefactos.dominio.artefacto.error.repositorio-ya-asignado-detalle", 1),
    ERROR_ESTUDIANTE_NO_PROPIETARIO("artefactos.dominio.artefacto.error.estudiante-no-propietario", 0),
    ERROR_ESTUDIANTE_NO_PROPIETARIO_DETALLE("artefactos.dominio.artefacto.error.estudiante-no-propietario-detalle", 2),
    ERROR_ASESOR_NO_PROPIETARIO("artefactos.dominio.artefacto.error.asesor-no-propietario", 0),
    ERROR_ASESOR_NO_PROPIETARIO_DETALLE("artefactos.dominio.artefacto.error.asesor-no-propietario-detalle", 2);

    private final String clave;
    private final int parametros;

    ArtefactoKey(String clave, int parametros) {
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
