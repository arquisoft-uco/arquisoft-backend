package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ArtefactoKey implements ClaveMensaje {

    LOG_VERSION_CERRADA_RECIBIDA("entregables.infraestructura.artefacto.log.version-cerrada-recibida", 4),
    LOG_OMISION_RECIBIDA("entregables.infraestructura.artefacto.log.omision-recibida", 3),
    LOG_REACTIVACION_RECIBIDA("entregables.infraestructura.artefacto.log.reactivacion-recibida", 3),
    LOG_VERIFICACION_REGISTRAR("entregables.aplicacion.artefacto.log.verificacion-registrar", 3),
    LOG_VERIFICACION_ORIGEN("entregables.aplicacion.artefacto.log.verificacion-origen", 1),
    LOG_VERIFICACION_OMISION("entregables.aplicacion.artefacto.log.verificacion-omision", 3),
    LOG_COPIADO("entregables.infraestructura.artefacto.log.copiado", 3),
    LOG_COPIA_ANTERIOR_ELIMINADA("entregables.infraestructura.artefacto.log.copia-anterior-eliminada", 2),
    LOG_GUARDADO("entregables.infraestructura.artefacto.log.guardado", 2),
    LOG_OMISION_ACTUALIZADA("entregables.infraestructura.artefacto.log.omision-actualizada", 2),
    LOG_REGISTRADA("entregables.infraestructura.artefacto.log.registrada", 1),
    LOG_OBSOLETA("entregables.infraestructura.artefacto.log.obsoleta", 1),
    LOG_PROYECTO_CERRADO("entregables.infraestructura.artefacto.log.proyecto-cerrado", 1),
    LOG_OMITIDO("entregables.infraestructura.artefacto.log.omitido", 1),
    LOG_REACTIVADO("entregables.infraestructura.artefacto.log.reactivado", 1),
    LOG_SIN_VERSION_CERRADA("entregables.infraestructura.artefacto.log.sin-version-cerrada", 1),
    LOG_DESCARTADA("entregables.infraestructura.artefacto.log.descartada", 1),
    LOG_PURGADOS("entregables.infraestructura.artefacto.log.purgados", 2),
    LOG_CONSULTANDO_VISTA_PREVIA("entregables.aplicacion.artefacto.log.consultando-vista-previa", 1),
    LOG_VISTA_PREVIA_CONSULTADA("entregables.aplicacion.artefacto.log.vista-previa-consultada", 2),
    ERROR_ARCHIVO_ORIGEN_NO_DISPONIBLE("entregables.dominio.artefacto.error.archivo-origen-no-disponible", 1),
    ERROR_ARCHIVO_ORIGEN_NO_DISPONIBLE_DETALLE(
            "entregables.dominio.artefacto.error.archivo-origen-no-disponible-detalle", 2),
    ERROR_SIN_ARTEFACTOS_PARA_PAQUETE("entregables.dominio.artefacto.error.sin-artefactos-para-paquete", 1),
    ERROR_SIN_ARTEFACTOS_PARA_PAQUETE_DETALLE(
            "entregables.dominio.artefacto.error.sin-artefactos-para-paquete-detalle", 1);

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
