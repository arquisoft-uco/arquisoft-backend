package com.arquisoft.notificaciones.domain.notificacion;

import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.message.constant.NotificacionesLimits;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class NotificacionDomain {

    private UUID id;
    private String idEvento;
    private TipoNotificacion tipo;
    private String destinatario;
    private String asunto;
    private String destinatarioNombre;
    private String cuerpo;
    private String pie;
    private EstadoNotificacion estado;
    private String detalleError;
    private Instant fechaCreacion;
    private Instant fechaEnvio;
    private int intentos;
    private Instant fechaUltimoIntento;

    private NotificacionDomain() {}

    public static NotificacionDomain crear(
            String idEvento, TipoNotificacion tipo, String destinatario, String asunto,
            String destinatarioNombre, String cuerpo, String pie) {

        var notificacion = new NotificacionDomain();
        var result = new ValidationResult();

        notificacion.setId();
        notificacion.setIdEvento(idEvento, result);
        notificacion.setTipo(tipo, result);
        notificacion.setDestinatario(destinatario, result);
        notificacion.setAsunto(asunto, result);
        notificacion.setDestinatarioNombre(destinatarioNombre, result);
        notificacion.setCuerpo(cuerpo, result);
        notificacion.setPie(pie);

        result.lanzarSiTieneErrores();

        notificacion.estado = EstadoNotificacion.PENDIENTE;
        notificacion.detalleError = UtilTexto.VACIO;
        notificacion.fechaCreacion = UtilFecha.generarInstanteActual();
        notificacion.fechaEnvio = UtilFecha.VACIO;
        notificacion.fechaUltimoIntento = UtilFecha.VACIO;
        return notificacion;
    }

    public static NotificacionDomain reconstruir(
            DatosNotificacion datos, EstadoNotificacion estado, String detalleError) {

        var notificacion = new NotificacionDomain();
        notificacion.id = UtilObjeto.aplicarPorDefecto(
                datos.id(), UtilUUID.obtenerUUIDPorDefecto());
        notificacion.idEvento = UtilTexto.aplicarTrim(datos.idEvento());
        notificacion.tipo = UtilObjeto.aplicarPorDefecto(datos.tipo(), TipoNotificacion.VACIO);
        notificacion.destinatario = UtilTexto.aplicarTrim(datos.destinatario());
        notificacion.asunto = UtilTexto.aplicarTrim(datos.asunto());
        notificacion.destinatarioNombre = UtilTexto.aplicarTrim(datos.destinatarioNombre());
        notificacion.cuerpo = UtilTexto.aplicarTrim(datos.cuerpo());
        notificacion.pie = UtilTexto.aplicarTrim(datos.pie());
        notificacion.fechaCreacion = UtilObjeto.aplicarPorDefecto(
                datos.fechaCreacion(), UtilFecha.VACIO);
        notificacion.fechaEnvio = UtilObjeto.aplicarPorDefecto(
                datos.fechaEnvio(), UtilFecha.VACIO);
        notificacion.intentos = datos.intentos();
        notificacion.fechaUltimoIntento = UtilObjeto.aplicarPorDefecto(
                datos.fechaUltimoIntento(), UtilFecha.VACIO);
        notificacion.estado = UtilObjeto.aplicarPorDefecto(estado, EstadoNotificacion.VACIO);
        notificacion.detalleError = UtilTexto.aplicarTrim(detalleError);
        return notificacion;
    }

    public record DatosNotificacion(
            UUID id,
            String idEvento,
            TipoNotificacion tipo,
            String destinatario,
            String asunto,
            String destinatarioNombre,
            String cuerpo,
            String pie,
            Instant fechaCreacion,
            Instant fechaEnvio,
            int intentos,
            Instant fechaUltimoIntento) {
    }

    public void marcarEnviada() {
        validarTransicionPermitida();
        this.estado = EstadoNotificacion.ENVIADA;
        this.detalleError = UtilTexto.VACIO;
        this.fechaEnvio = UtilFecha.generarInstanteActual();
    }

    public void marcarFallida(String motivo) {
        validarTransicionPermitida();
        this.estado = EstadoNotificacion.FALLIDA;
        this.detalleError = UtilTexto.aplicarTrim(motivo);
        this.fechaEnvio = UtilFecha.generarInstanteActual();
    }

    public void prepararReintento() {
        if (this.estado != EstadoNotificacion.FALLIDA) {
            var result = new ValidationResult();
            result.agregarError(
                    NotificacionesFields.Notificacion.ESTADO,
                    NotificacionesCodes.Notificacion.REINTENTO_NO_PERMITIDO,
                    Mensajes.formatear(
                            NotificacionKey.ERROR_REINTENTO_NO_PERMITIDO, this.estado)
            );
            result.lanzarSiTieneErrores();
        }
        this.estado = EstadoNotificacion.PENDIENTE;
        this.detalleError = UtilTexto.VACIO;
        this.intentos = this.intentos + 1;
        this.fechaUltimoIntento = UtilFecha.generarInstanteActual();
    }

    private void validarTransicionPermitida() {
        if (this.estado.esTerminal()) {
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
        var tipoSeguro = UtilObjeto.aplicarPorDefecto(tipo, TipoNotificacion.VACIO);
        if (tipoSeguro.esVacio()) {
            result.agregarError(
                    NotificacionesFields.Notificacion.TIPO,
                    NotificacionesCodes.Notificacion.TIPO_REQUERIDO,
                    Mensajes.formatear(
                            ValidadorKey.NO_NULO, NotificacionesFields.Notificacion.TIPO));
            return;
        }
        this.tipo = tipoSeguro;
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

    private void setDestinatarioNombre(String destinatarioNombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(destinatarioNombre,
                NotificacionesFields.Notificacion.DESTINATARIO_NOMBRE,
                NotificacionesCodes.Notificacion.DESTINATARIO_NOMBRE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(destinatarioNombre,
                NotificacionesLimits.Notificacion.DESTINATARIO_NOMBRE_MAX,
                NotificacionesFields.Notificacion.DESTINATARIO_NOMBRE,
                NotificacionesCodes.Notificacion.DESTINATARIO_NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.destinatarioNombre = UtilTexto.aplicarTrim(destinatarioNombre);
    }

    private void setPie(String pie) {
        this.pie = UtilTexto.aplicarTrim(pie);
    }

    private void setCuerpo(String cuerpo, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(cuerpo,
                NotificacionesFields.Notificacion.CUERPO,
                NotificacionesCodes.Notificacion.CUERPO_REQUERIDO, result)) {
            return;
        }
        this.cuerpo = UtilTexto.aplicarTrim(cuerpo);
    }

    public UUID getId() {
        return id;
    }

    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public String getPie() {
        return pie;
    }

    public int getIntentos() {
        return intentos;
    }

    public Instant getFechaUltimoIntento() {
        return fechaUltimoIntento;
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
