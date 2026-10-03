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

import java.time.Instant;
import java.util.UUID;

public final class SolicitudDomain {

    public static final SolicitudDomain VACIO = new SolicitudDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilFecha.VACIO,
            UtilTexto.VACIO,
            TipoSolicitud.VACIO);

    private UUID id;
    private UUID destinatarioUsuario;
    private UUID remitenteUsuario;
    private Instant fechaCreacion;
    private String mensajeSolicitud;
    private TipoSolicitud tipoSolicitud;

    private SolicitudDomain() {}

    private SolicitudDomain(UUID id, UUID destinatarioUsuario, UUID remitenteUsuario, Instant fechaCreacion,
                            String mensajeSolicitud, TipoSolicitud tipoSolicitud) {
        this.id = id;
        this.destinatarioUsuario = destinatarioUsuario;
        this.remitenteUsuario = remitenteUsuario;
        this.fechaCreacion = fechaCreacion;
        this.mensajeSolicitud = mensajeSolicitud;
        this.tipoSolicitud = tipoSolicitud;
    }

    public static SolicitudDomain crear(UUID destinatarioUsuario, UUID remitenteUsuario,
                                        String mensajeSolicitud, TipoSolicitud tipoSolicitud) {
        var solicitud = new SolicitudDomain();
        var result = new ValidationResult();

        solicitud.setId();
        solicitud.setFechaCreacion();
        solicitud.setDestinatarioUsuario(destinatarioUsuario, result);
        solicitud.setRemitenteUsuario(remitenteUsuario, result);
        solicitud.setMensajeSolicitud(mensajeSolicitud, result);
        solicitud.setTipoSolicitud(tipoSolicitud, result);

        result.lanzarSiTieneErrores();
        return solicitud;
    }

    public static SolicitudDomain reconstruir(UUID id, UUID destinatarioUsuario, UUID remitenteUsuario,
                                              Instant fechaCreacion, String mensajeSolicitud,
                                              TipoSolicitud tipoSolicitud) {
        return new SolicitudDomain(id, destinatarioUsuario, remitenteUsuario, fechaCreacion,
                mensajeSolicitud, tipoSolicitud);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setFechaCreacion() {
        this.fechaCreacion = UtilFecha.generarInstanteActual();
    }

    private void setDestinatarioUsuario(UUID destinatarioUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(destinatarioUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        this.destinatarioUsuario = destinatarioUsuario;
    }

    private void setRemitenteUsuario(UUID remitenteUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(remitenteUsuario,
                SolicitudesFields.Solicitud.REMITENTE,
                SolicitudesCodes.Solicitud.REMITENTE_REQUERIDO, result)) {
            return;
        }
        this.remitenteUsuario = remitenteUsuario;
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

    public UUID getDestinatarioUsuario() {
        return destinatarioUsuario;
    }

    public UUID getRemitenteUsuario() {
        return remitenteUsuario;
    }

    public Instant getFechaCreacion() {
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
