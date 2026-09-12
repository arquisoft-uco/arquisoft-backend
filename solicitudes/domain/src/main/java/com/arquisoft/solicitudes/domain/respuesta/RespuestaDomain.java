package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.message.constant.SolicitudesLimits;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;

import java.time.LocalDateTime;
import java.util.UUID;

public final class RespuestaDomain {

    public static final RespuestaDomain VACIO = new RespuestaDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilFecha.FECHA_HORA_VACIA,
            UtilTexto.VACIO,
            EstadoRespuesta.VACIO);

    private UUID id;
    private UUID solicitud;
    private LocalDateTime fechaRespuesta;
    private String contenido;
    private EstadoRespuesta estadoRespuesta;

    private RespuestaDomain() {}

    private RespuestaDomain(UUID id, UUID solicitud, LocalDateTime fechaRespuesta,
                            String contenido, EstadoRespuesta estadoRespuesta) {
        this.id = id;
        this.solicitud = solicitud;
        this.fechaRespuesta = fechaRespuesta;
        this.contenido = contenido;
        this.estadoRespuesta = estadoRespuesta;
    }

    public static RespuestaDomain crear(UUID solicitud, String contenido) {
        var respuesta = new RespuestaDomain();
        var result = new ValidationResult();

        respuesta.setId();
        respuesta.setFechaRespuesta();
        respuesta.setSolicitud(solicitud, result);
        respuesta.setContenido(contenido, result);
        respuesta.setEstadoRespuesta();

        result.lanzarSiTieneErrores();
        return respuesta;
    }

    public static RespuestaDomain reconstruir(UUID id, UUID solicitud, LocalDateTime fechaRespuesta,
                                              String contenido, EstadoRespuesta estadoRespuesta) {
        return new RespuestaDomain(id, solicitud, fechaRespuesta, contenido, estadoRespuesta);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setFechaRespuesta() {
        this.fechaRespuesta = UtilFecha.generarFechaHoraActual();
    }

    private void setSolicitud(UUID solicitud, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(solicitud,
                SolicitudesFields.Respuesta.SOLICITUD,
                SolicitudesCodes.Respuesta.SOLICITUD_REQUERIDO, result)) {
            return;
        }
        this.solicitud = solicitud;
    }

    private void setContenido(String contenido, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(contenido);
        if (!ValidatorTexto.noEnBlanco(recortado,
                SolicitudesFields.Respuesta.CONTENIDO,
                SolicitudesCodes.Respuesta.CONTENIDO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudEntre(recortado,
                SolicitudesLimits.Respuesta.CONTENIDO_MIN, SolicitudesLimits.Respuesta.CONTENIDO_MAX,
                SolicitudesFields.Respuesta.CONTENIDO,
                SolicitudesCodes.Respuesta.CONTENIDO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.contenido = recortado;
    }

    private void setEstadoRespuesta() {
        this.estadoRespuesta = EstadoRespuesta.EN_REVISION;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSolicitud() {
        return solicitud;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public String getContenido() {
        return contenido;
    }

    public EstadoRespuesta getEstadoRespuesta() {
        return estadoRespuesta;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
