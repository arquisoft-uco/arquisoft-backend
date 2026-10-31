package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.notificaciones.domain.notificacion.exception.TipoNotificacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum TipoNotificacion {

    ASESOR_FICHA_CAMBIADO,
    FICHA_PERFIL_REGISTRADA_ASESOR,
    ESTUDIANTES_FICHA_PERFIL_ASIGNADOS,
    SOLICITUD_NOVEDAD_COORDINADOR,
    SOLICITUD_NOVEDAD_COORDINADOR_RESPONDIDA,
    SOLICITUD_NOVEDAD_COORDINADOR_ESTADO_MODIFICADO,
    SOLICITUD_NOVEDAD_ASESOR_ESTADO_MODIFICADO,
    SOLICITUD_CAMBIO_ASESOR_ESTADO_MODIFICADO,
    SOLICITUD_AMPLIACION_PLAZO_ESTADO_MODIFICADO,
    SOLICITUD_REGISTRO_MODIFICACION_USUARIOS_ESTADO_MODIFICADO,
    SOLICITUD_NOVEDAD_ASESOR,
    SOLICITUD_NOVEDAD_ASESOR_RESPONDIDA,
    SOLICITUD_CAMBIO_ASESOR_RESPONDIDA,
    SOLICITUD_AMPLIACION_PLAZO_RESPONDIDA,
    SOLICITUD_REGISTRO_MODIFICACION_USUARIOS_RESPONDIDA,
    SOLICITUD_CAMBIO_ASESOR,
    SOLICITUD_AMPLIACION_PLAZO,
    SOLICITUD_REGISTRO_MODIFICACION_USUARIOS,
    REVISION_ITEM_AGREGADO,
    USUARIO_ESTADO_CAMBIADO,
    FICHA_PERFIL_APROBADA_ESTUDIANTE,
    FICHA_PERFIL_APROBADA_ASESOR,
    FICHA_PERFIL_NO_APROBADA_ESTUDIANTE,
    FICHA_PERFIL_NO_APROBADA_ASESOR,
    PROYECTO_GRADO_REGISTRADO_COORDINADOR,
    ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS,
    ASESOR_PROYECTO_GRADO_ASIGNADO,
    ASESOR_PROYECTO_GRADO_FINALIZADO,
    ESTADO_FICHA_PERFIL_AGREGADO,
    REVISION_ASESOR_RESUELTA,
    VERSION_ARTEFACTO_GENERADA,
    ESTUDIANTES_ARTEFACTO_ASIGNADOS,
    ARTEFACTO_OMITIDO,
    REVISION_ASESOR_ENVIADA,
    OBSERVACION_REABIERTA,

    VACIO;

    public String getId() {
        return name();
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public static TipoNotificacion desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new TipoNotificacionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<TipoNotificacion> delCatalogo(String id) {
        return UtilEnum.desde(TipoNotificacion.class, id).filter(tipo -> tipo != VACIO);
    }
}
