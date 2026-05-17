package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.util.message.SeguridadApplicationMessages;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.util.UtilObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Comando inmutable que representa la intencion de revocar un token JWT.
 * Se construye solo cuando el JTI es no nulo y el token tiene vida restante > 0.
 *
 * <p>Las validaciones de este constructor son defensivas: el controlador
 * ya guarda estos invariantes antes de crear el comando. Si se violan,
 * indica un fallo en la capa de llamada.</p>
 */
@Slf4j
public record LogoutCommand(String jti, long ttlSegundos) {

    public LogoutCommand {
        if (UtilObject.isNull(jti)) {
            // log.warn: error de cliente — la guardia del controlador no funciono
            log.warn(SeguridadApplicationMessages.LogoutCommand.JTI_NULL_LOG);
            // ApplicationException: mensaje generico para el cliente (HTTP 400)
            throw new ApplicationException(
                    SeguridadApplicationMessages.LogoutCommand.DATOS_SESION_INVALIDOS,
                    SeguridadApplicationMessages.LogoutCommand.CODIGO_SESION_INVALIDA);
        }
        if (ttlSegundos <= 0) {
            // log.warn: error de cliente — TTL invalido, tecnico para el desarrollador
            log.warn(SeguridadApplicationMessages.LogoutCommand.TTL_INVALIDO_LOG, ttlSegundos);
            // ApplicationException: mismo mensaje generico para el cliente (HTTP 400)
            throw new ApplicationException(
                    SeguridadApplicationMessages.LogoutCommand.DATOS_SESION_INVALIDOS,
                    SeguridadApplicationMessages.LogoutCommand.CODIGO_SESION_INVALIDA);
        }
    }
}
