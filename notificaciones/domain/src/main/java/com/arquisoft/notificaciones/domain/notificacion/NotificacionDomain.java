package com.arquisoft.notificaciones.domain.notificacion;

import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.message.constant.NotificacionesLimits;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class NotificacionDomain {

    private UUID id;
    private String idEvento;
    private TipoNotificacion tipo;
    private String destinatario;
    private String asunto;
    private EstadoNotificacion estado;
    private String detalleError;
    private Instant fechaCreacion;
    private Instant fechaEnvio;

    private NotificacionDomain() {}

    public static NotificacionDomain crear(
            String idEvento, TipoNotificacion tipo, String destinatario, String asunto) {

        var notificacion = new NotificacionDomain();
        var result = new ValidationResult();

        notificacion.setId();
        notificacion.setIdEvento(idEvento, result);
        notificacion.setTipo(tipo, result);
        notificacion.setDestinatario(destinatario, result);
        notificacion.setAsunto(asunto, result);

        result.lanzarSiTieneErrores();

        notificacion.estado = EstadoNotificacion.PENDIENTE;
        notificacion.fechaCreacion = UtilFecha.generarInstanteActual();
        return notificacion;
    }

    public static NotificacionDomain reconstruir(
            DatosNotificacion datos, EstadoNotificacion estado, String detalleError) {

        var notificacion = new NotificacionDomain();
        notificacion.id = datos.id();
        notificacion.idEvento = datos.idEvento();
        notificacion.tipo = datos.tipo();
        notificacion.destinatario = datos.destinatario();
        notificacion.asunto = datos.asunto();
        notificacion.fechaCreacion = datos.fechaCreacion();
        notificacion.fechaEnvio = datos.fechaEnvio();
        notificacion.estado = estado;
        notificacion.detalleError = detalleError;
        return notificacion;
    }

    public record DatosNotificacion(
            UUID id,
            String idEvento,
            TipoNotificacion tipo,
            String destinatario,
            String asunto,
            Instant fechaCreacion,
            Instant fechaEnvio) {
    }

    public void marcarEnviada() {
        validarTransicionPermitida();
        this.estado = EstadoNotificacion.ENVIADA;
        this.detalleError = null;
        this.fechaEnvio = UtilFecha.generarInstanteActual();
    }

    public void marcarFallida(String motivo) {
        validarTransicionPermitida();
        this.estado = EstadoNotificacion.FALLIDA;
        this.detalleError = motivo;
        this.fechaEnvio = UtilFecha.generarInstanteActual();
    }

    private void validarTransicionPermitida() {
        if (this.estado != null && this.estado.esTerminal()) {
            var result = new ValidationResult();
            result.agregarError(
                    NotificacionesFields.Notificacion.ESTADO,
                    NotificacionesCodes.Notificacion.TRANSICION_INVALIDA,
                    Mensajes.formatear(
                            NotificacionKey.ERROR_TRANSICION_INVALIDA, this.estado)
            );
            result.lanzarSiTieneErrores();
        }
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setIdEvento(String idEvento, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(idEvento,
                NotificacionesFields.Notificacion.ID_EVENTO,
                NotificacionesCodes.Notificacion.ID_EVENTO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(idEvento,
                NotificacionesLimits.Notificacion.ID_EVENTO_MAX,
                NotificacionesFields.Notificacion.ID_EVENTO,
                NotificacionesCodes.Notificacion.ID_EVENTO_REQUERIDO, result)) {
            return;
        }
        this.idEvento = UtilTexto.aplicarTrim(idEvento);
    }

    private void setTipo(TipoNotificacion tipo, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tipo,
                NotificacionesFields.Notificacion.TIPO,
                NotificacionesCodes.Notificacion.TIPO_REQUERIDO, result)) {
            return;
        }
        this.tipo = tipo;
    }

    private void setDestinatario(String destinatario, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(destinatario,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorTexto.correoValido(destinatario,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_INVALIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(destinatario,
                NotificacionesLimits.Notificacion.DESTINATARIO_MAX,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_INVALIDO, result)) {
            return;
        }
        this.destinatario = UtilTexto.aplicarTrim(destinatario);
    }

    private void setAsunto(String asunto, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(asunto,
                NotificacionesFields.Notificacion.ASUNTO,
                NotificacionesCodes.Notificacion.ASUNTO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(asunto,
                NotificacionesLimits.Notificacion.ASUNTO_MAX,
                NotificacionesFields.Notificacion.ASUNTO,
                NotificacionesCodes.Notificacion.ASUNTO_REQUERIDO, result)) {
            return;
        }
        this.asunto = UtilTexto.aplicarTrim(asunto);
    }

    public UUID getId() {
        return id;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public String getDetalleError() {
        return detalleError;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaEnvio() {
        return fechaEnvio;
    }
}
