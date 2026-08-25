package com.arquisoft.seguridad.application.auth.command.primaryport.model;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public record AutenticarUsuarioCommand(
        String email,
        String contrasena
) {
    private static final String MASCARA_CONTRASENA = "****";
    private static final String FORMATO = "AutenticarUsuarioCommand[email=%s, contrasena=%s]";

    public AutenticarUsuarioCommand {
        email = UtilTexto.aplicarTrim(email);
    }

    public static AutenticarUsuarioCommand crear(String email, String contrasena) {
        var result = new ValidationResult();
        // Se recorta antes de validar: el constructor compacto lo hace despues, y sin esto
        // un correo con espacios alrededor fallaria el formato por los espacios.
        var correo = UtilTexto.aplicarTrim(email);

        if (ValidatorTexto.noEnBlanco(correo,
                SeguridadFields.Autenticacion.EMAIL, SeguridadCodes.Autenticacion.EMAIL_REQUERIDO, result)) {
            ValidatorTexto.correoValido(correo,
                    SeguridadFields.Autenticacion.EMAIL,
                    SeguridadCodes.Autenticacion.EMAIL_FORMATO_INVALIDO, result);
        }

        if (ValidatorTexto.noEnBlanco(contrasena,
                SeguridadFields.Autenticacion.CONTRASENA,
                SeguridadCodes.Autenticacion.CONTRASENA_REQUERIDA, result)) {
            ValidatorLongitud.longitudMinima(contrasena, SeguridadLimits.Autenticacion.CONTRASENA_MIN,
                    SeguridadFields.Autenticacion.CONTRASENA,
                    SeguridadCodes.Autenticacion.CONTRASENA_DEMASIADO_CORTA, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new AutenticarUsuarioCommand(correo, contrasena);
    }

    // El toString que genera el compilador para un record incluye todos sus componentes:
    // un log del command volcaria la contrasena en claro.
    @Override
    public String toString() {
        return FORMATO.formatted(email, MASCARA_CONTRASENA);
    }
}
