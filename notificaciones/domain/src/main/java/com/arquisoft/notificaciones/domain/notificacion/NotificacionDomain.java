package com.arquisoft.notificaciones.domain.notificacion;

import com.arquisoft.notificaciones.domain.notificacion.model.Contenido;
import com.arquisoft.notificaciones.domain.notificacion.model.Destinatario;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.message.constant.NotificacionesLimits;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class NotificacionDomain {

    private UUID id;
    private String idEvento;
    private TipoNotificacion tipo;
    private Destinatario destinatario;
    private Contenido contenido;
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
        notificacion.destinatario = Destinatario.crear(destinatarioNombre, destinatario, result);
        notificacion.contenido = Contenido.crear(asunto, cuerpo, pie, result);

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
        notificacion.destinatario = Destinatario.reconstruir(
                datos.destinatarioNombre(), datos.destinatario());
        notificacion.contenido = Contenido.reconstruir(
                datos.asunto(), datos.cuerpo(), datos.pie());
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

    public UUID getId() {
        return id;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public Destinatario getDestinatario() {
        return destinatario;
    }

    public Contenido getContenido() {
        return contenido;
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

    public int getIntentos() {
        return intentos;
    }

    public Instant getFechaUltimoIntento() {
        return fechaUltimoIntento;
    }
}
