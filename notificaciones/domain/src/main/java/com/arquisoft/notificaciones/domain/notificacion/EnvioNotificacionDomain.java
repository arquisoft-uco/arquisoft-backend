package com.arquisoft.notificaciones.domain.notificacion;

import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

public final class EnvioNotificacionDomain {

    private NotificacionDomain notificacion;
    private String destinatarioNombre;
    private String cuerpo;

    private EnvioNotificacionDomain() {}

    public static EnvioNotificacionDomain crear(NotificacionDomain notificacion,
                                                String destinatarioNombre,
                                                String cuerpo) {
        var envio = new EnvioNotificacionDomain();
        var result = new ValidationResult();

        envio.setNotificacion(notificacion, result);
        envio.setDestinatarioNombre(destinatarioNombre, result);
        envio.setCuerpo(cuerpo, result);

        result.lanzarSiTieneErrores();
        return envio;
    }

    private void setNotificacion(NotificacionDomain notificacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(notificacion,
                NotificacionesFields.Notificacion.ID_EVENTO,
                NotificacionesCodes.Notificacion.ID_EVENTO_REQUERIDO, result)) {
            return;
        }
        this.notificacion = notificacion;
    }

    private void setDestinatarioNombre(String destinatarioNombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(destinatarioNombre,
                NotificacionesFields.Notificacion.DESTINATARIO_NOMBRE,
                NotificacionesCodes.Notificacion.DESTINATARIO_NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.destinatarioNombre = UtilTexto.aplicarTrim(destinatarioNombre);
    }

    private void setCuerpo(String cuerpo, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(cuerpo,
                NotificacionesFields.Notificacion.CUERPO,
                NotificacionesCodes.Notificacion.CUERPO_REQUERIDO, result)) {
            return;
        }
        this.cuerpo = UtilTexto.aplicarTrim(cuerpo);
    }

    public NotificacionDomain getNotificacion() {
        return notificacion;
    }

    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public String getIdEvento() {
        return notificacion.getIdEvento();
    }

    public String getDestinatarioEmail() {
        return notificacion.getDestinatario();
    }

    public String getAsunto() {
        return notificacion.getAsunto();
    }
}
