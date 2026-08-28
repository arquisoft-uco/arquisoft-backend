package com.arquisoft.solicitudes.domain.solicitud;

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
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

import java.time.LocalDateTime;
import java.util.UUID;

public final class SolicitudDomain {

    public static final SolicitudDomain VACIO = new SolicitudDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilFecha.FECHA_HORA_VACIA,
            UtilTexto.VACIO,
            TipoSolicitud.VACIO);

    private UUID id;
    private UUID destinatario;
    private UUID remitente;
    private LocalDateTime fechaCreacion;
    private String mensajeSolicitud;
    private TipoSolicitud tipoSolicitud;

    private SolicitudDomain() {}

    private SolicitudDomain(UUID id, UUID destinatario, UUID remitente, LocalDateTime fechaCreacion,
                            String mensajeSolicitud, TipoSolicitud tipoSolicitud) {
        this.id = id;
        this.destinatario = destinatario;
        this.remitente = remitente;
        this.fechaCreacion = fechaCreacion;
        this.mensajeSolicitud = mensajeSolicitud;
        this.tipoSolicitud = tipoSolicitud;
    }

    public static SolicitudDomain crear(UUID destinatario, UUID remitente,
                                        String mensajeSolicitud, TipoSolicitud tipoSolicitud) {
        var solicitud = new SolicitudDomain();
        var result = new ValidationResult();

        solicitud.setId();
        solicitud.setFechaCreacion();
        solicitud.setDestinatario(destinatario, result);
        solicitud.setRemitente(remitente, result);
        solicitud.setMensajeSolicitud(mensajeSolicitud, result);
        solicitud.setTipoSolicitud(tipoSolicitud, result);

        result.lanzarSiTieneErrores();
        return solicitud;
    }

    public static SolicitudDomain reconstruir(UUID id, UUID destinatario, UUID remitente,
                                              LocalDateTime fechaCreacion, String mensajeSolicitud,
                                              TipoSolicitud tipoSolicitud) {
        return new SolicitudDomain(id, destinatario, remitente, fechaCreacion, mensajeSolicitud, tipoSolicitud);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setFechaCreacion() {
        this.fechaCreacion = UtilFecha.generarFechaHoraActual();
    }

    private void setDestinatario(UUID destinatario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(destinatario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        this.destinatario = destinatario;
    }

    private void setRemitente(UUID remitente, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(remitente,
                SolicitudesFields.Solicitud.REMITENTE,
                SolicitudesCodes.Solicitud.REMITENTE_REQUERIDO, result)) {
            return;
        }
        this.remitente = remitente;
    }

    private void setMensajeSolicitud(String mensajeSolicitud, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(mensajeSolicitud,
                SolicitudesFields.Solicitud.MENSAJE,
                SolicitudesCodes.Solicitud.MENSAJE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudEntre(mensajeSolicitud,
                SolicitudesLimits.Solicitud.MENSAJE_MIN, SolicitudesLimits.Solicitud.MENSAJE_MAX,
                SolicitudesFields.Solicitud.MENSAJE,
                SolicitudesCodes.Solicitud.MENSAJE_DEMASIADO_LARGO, result)) {
            return;
        }
        this.mensajeSolicitud = UtilTexto.aplicarTrim(mensajeSolicitud);
    }

    private void setTipoSolicitud(TipoSolicitud tipoSolicitud, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tipoSolicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result)) {
            return;
        }
        this.tipoSolicitud = tipoSolicitud;
    }

    public UUID getId() {
        return id;
    }

    public UUID getDestinatario() {
        return destinatario;
    }

    public UUID getRemitente() {
        return remitente;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getMensajeSolicitud() {
        return mensajeSolicitud;
    }

    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
