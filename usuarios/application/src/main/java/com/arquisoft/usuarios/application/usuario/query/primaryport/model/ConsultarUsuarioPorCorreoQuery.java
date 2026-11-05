package com.arquisoft.usuarios.application.usuario.query.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.message.constant.UsuariosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public record ConsultarUsuarioPorCorreoQuery(String correo) {

    public static ConsultarUsuarioPorCorreoQuery crear(String correo) {
        var result = new ValidationResult();
        var recortado = UtilTexto.aplicarTrim(correo);

        if (ValidatorTexto.noEnBlanco(recortado, UsuariosFields.Usuario.CORREO,
                UsuariosCodes.Usuario.EMAIL_REQUERIDO, result)
                && ValidatorLongitud.longitudMaxima(recortado, UsuariosLimits.Usuario.EMAIL_MAX,
                UsuariosFields.Usuario.CORREO, UsuariosCodes.Usuario.EMAIL_LONGITUD, result)) {
            ValidatorTexto.correoValido(recortado, UsuariosFields.Usuario.CORREO,
                    UsuariosCodes.Usuario.EMAIL_FORMATO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarUsuarioPorCorreoQuery(recortado);
    }
}
