package com.arquisoft.usuarios.application.administrador.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverAdministradorCommand(UUID usuario, UUID actor) {

    public static RemoverAdministradorCommand crear(UUID usuario, UUID actor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.Administrador.USUARIO,
                UsuariosCodes.Administrador.USUARIO_REQUERIDO, result);
        ValidatorObjeto.noNulo(actor, UsuariosFields.Administrador.ACTOR,
                UsuariosCodes.Administrador.ACTOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverAdministradorCommand(usuario, actor);
    }
}
