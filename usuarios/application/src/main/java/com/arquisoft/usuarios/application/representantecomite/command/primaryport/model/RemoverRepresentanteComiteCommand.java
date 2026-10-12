package com.arquisoft.usuarios.application.representantecomite.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverRepresentanteComiteCommand(UUID usuario) {

    public static RemoverRepresentanteComiteCommand crear(UUID usuario) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.RepresentanteComite.USUARIO,
                UsuariosCodes.RepresentanteComite.USUARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverRepresentanteComiteCommand(usuario);
    }
}
