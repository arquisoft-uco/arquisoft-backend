package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record EliminarUsuarioCommand(UUID usuario) {

    public static EliminarUsuarioCommand crear(UUID usuario) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.Usuario.USUARIO,
                UsuariosCodes.Usuario.USUARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new EliminarUsuarioCommand(usuario);
    }
}
