package com.arquisoft.seguridad.application.auth.dto;

import com.arquisoft.seguridad.application.util.message.SeguridadApplicationMessages;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.util.UtilObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

/**
 * DTO inmutable que transporta los datos necesarios para invalidar un token JWT.
 *
 * <p><b>Nota sobre validaciones:</b> las anotaciones {@code @NotNull} y {@code @Positive}
 * documentan el contrato de este DTO pero <em>no se activan automaticamente al instanciar</em>
 * — Jakarta Validation requiere un {@code Validator} externo (via {@code @Valid} en Spring MVC
 * o invocacion programatica). Como el endpoint {@code /auth/logout} no puede usar {@code @Valid}
 * (un token expirado llega con jti null o ttl <= 0 y debe manejarse como sesion ya cerrada,
 * no como error de validacion), la validacion real ocurre en el constructor compacto del record,
 * que lanza {@link ApplicationException} si se violan los invariantes.</p>
 *
 * <p>El controlador aplica sus propias guardias antes de construir este DTO, por lo que
 * el constructor compacto actua como segunda linea defensiva.</p>
 */
@Slf4j
public record LogoutRequestDTO(
        @NotNull(message = "El JTI del token es requerido")
        String jti,

        @Positive(message = "Los segundos restantes deben ser mayores a cero")
        long ttlSegundos) {

    public LogoutRequestDTO {
        if (UtilObject.isNull(jti)) {
            // log.warn: error de cliente — la guardia del controlador no funciono
            log.warn(SeguridadApplicationMessages.LogoutRequestDTO.JTI_NULL_LOG);
            // ApplicationException: mensaje generico para el cliente (HTTP 400)
            throw new ApplicationException(
                    SeguridadApplicationMessages.LogoutRequestDTO.DATOS_SESION_INVALIDOS,
                    SeguridadApplicationMessages.LogoutRequestDTO.CODIGO_SESION_INVALIDA);
        }
        if (ttlSegundos <= 0) {
            // log.warn: error de cliente — TTL invalido, tecnico para el desarrollador
            log.warn(SeguridadApplicationMessages.LogoutRequestDTO.TTL_INVALIDO_LOG, ttlSegundos);
            // ApplicationException: mismo mensaje generico para el cliente (HTTP 400)
            throw new ApplicationException(
                    SeguridadApplicationMessages.LogoutRequestDTO.DATOS_SESION_INVALIDOS,
                    SeguridadApplicationMessages.LogoutRequestDTO.CODIGO_SESION_INVALIDA);
        }
    }
}
