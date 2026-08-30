package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;

public record CrearUsuarioCommand(
        String email,
        UsuarioRole rol
) {
    public static CrearUsuarioCommand crear(String email, String rol) {
        var result = new ValidationResult();

        ValidatorTexto.noEnBlanco(email,
                UsuariosFields.Usuario.EMAIL, UsuariosCodes.Usuario.EMAIL_REQUERIDO, result);

        boolean rolPresente = ValidatorTexto.noEnBlanco(rol,
                UsuariosFields.Usuario.ROL, UsuariosCodes.Usuario.ROL_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CrearUsuarioCommand(
                UtilTexto.aplicarTrim(email),
                rolPresente ? UsuarioRole.desdeCodigo(rol) : null);
    }
}
