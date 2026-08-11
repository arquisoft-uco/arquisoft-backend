package com.arquisoft.notificaciones.domain.notificacion;

import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.message.constant.NotificacionesLimits;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro de una notificacion: a quien se le aviso, por que, y como termino el intento.
 *
 * <p>Existe para dos cosas que el envio por si solo no da: idempotencia —{@code idEvento} es unico,
 * de modo que un reintento del broker no genera un segundo correo— y auditoria de lo que se
 * entrego.
 */
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
        notificacion.fechaCreacion = Instant.now();
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

    /**
     * Datos de reconstruccion agrupados.
     *
     * <p>Van juntos porque {@code reconstruir} superaria el limite de parametros del proyecto si se
     * pasaran sueltos, y porque describen una sola cosa: la fila leida de la tabla.
     *
     * @param id            identificador de la notificacion
     * @param idEvento      evento de dominio que la origino
     * @param tipo          motivo de la notificacion
     * @param destinatario  correo al que se dirige
     * @param asunto        linea de asunto
     * @param fechaCreacion momento en que se registro
     * @param fechaEnvio    momento de la entrega, o {@code null} si aun no se resolvio
     */
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
        this.fechaEnvio = Instant.now();
    }

    public void marcarFallida(String motivo) {
        validarTransicionPermitida();
        this.estado = EstadoNotificacion.FALLIDA;
        this.detalleError = motivo;
        this.fechaEnvio = Instant.now();
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
        this.id = UtilUUID.generateNewUUID();
    }

    private void setIdEvento(String idEvento, ValidationResult result) {
        if (!DomainValidator.noEnBlanco(idEvento,
                NotificacionesFields.Notificacion.ID_EVENTO,
                NotificacionesCodes.Notificacion.ID_EVENTO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.longitudMaxima(idEvento,
                NotificacionesLimits.Notificacion.ID_EVENTO_MAX,
                NotificacionesFields.Notificacion.ID_EVENTO,
                NotificacionesCodes.Notificacion.ID_EVENTO_REQUERIDO, result)) {
            return;
        }
        this.idEvento = UtilText.applyTrim(idEvento);
    }

    private void setTipo(TipoNotificacion tipo, ValidationResult result) {
        if (!DomainValidator.noNulo(tipo,
                NotificacionesFields.Notificacion.TIPO,
                NotificacionesCodes.Notificacion.TIPO_REQUERIDO, result)) {
            return;
        }
        this.tipo = tipo;
    }

    private void setDestinatario(String destinatario, ValidationResult result) {
        if (!DomainValidator.noEnBlanco(destinatario,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.correoValido(destinatario,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_INVALIDO, result)) {
            return;
        }
        if (!DomainValidator.longitudMaxima(destinatario,
                NotificacionesLimits.Notificacion.DESTINATARIO_MAX,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_INVALIDO, result)) {
            return;
        }
        this.destinatario = UtilText.applyTrim(destinatario);
    }

    private void setAsunto(String asunto, ValidationResult result) {
        if (!DomainValidator.noEnBlanco(asunto,
                NotificacionesFields.Notificacion.ASUNTO,
                NotificacionesCodes.Notificacion.ASUNTO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.longitudMaxima(asunto,
                NotificacionesLimits.Notificacion.ASUNTO_MAX,
                NotificacionesFields.Notificacion.ASUNTO,
                NotificacionesCodes.Notificacion.ASUNTO_REQUERIDO, result)) {
            return;
        }
        this.asunto = UtilText.applyTrim(asunto);
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
