package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EliminarVersionArtefactoKey implements ClaveMensaje {

    ERROR_NO_ES_ULTIMA("artefactos.dominio.versionartefacto.error.no-es-ultima-para-eliminar", 1),
    ERROR_NO_ES_ULTIMA_DETALLE("artefactos.dominio.versionartefacto.error.no-es-ultima-para-eliminar-detalle", 2),
    ERROR_CON_REVISION("artefactos.dominio.versionartefacto.error.con-revision", 1),
    ERROR_CON_REVISION_DETALLE("artefactos.dominio.versionartefacto.error.con-revision-detalle", 2),
    ERROR_REVISION_ANTERIOR_NO_PERMITE(
            "artefactos.dominio.versionartefacto.error.revision-anterior-no-permite-eliminar", 2),
    ERROR_REVISION_ANTERIOR_NO_PERMITE_DETALLE(
            "artefactos.dominio.versionartefacto.error.revision-anterior-no-permite-eliminar-detalle", 2),
    LOG_ELIMINANDO("artefactos.aplicacion.eliminarversionartefacto.log.eliminando", 2),
    LOG_VERIFICACION_ELIMINAR("artefactos.aplicacion.eliminarversionartefacto.log.verificacion-eliminar", 5),
    LOG_ELIMINADA("artefactos.aplicacion.eliminarversionartefacto.log.eliminada", 3),
    LOG_ELIMINADO("artefactos.infraestructura.eliminarversionartefacto.log.eliminado", 1),
    LOG_ARCHIVO_ELIMINADO("artefactos.infraestructura.eliminarversionartefacto.log.archivo-eliminado", 1);

    private final String clave;
    private final int parametros;

    EliminarVersionArtefactoKey(String clave, int parametros) {
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
