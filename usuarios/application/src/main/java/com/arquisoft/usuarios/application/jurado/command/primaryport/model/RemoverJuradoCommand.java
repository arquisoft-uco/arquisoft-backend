package com.arquisoft.usuarios.application.jurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverJuradoCommand(UUID usuario) {

    public static RemoverJuradoCommand crear(UUID usuario) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.Jurado.USUARIO,
                UsuariosCodes.Jurado.USUARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverJuradoCommand(usuario);
    }
}
