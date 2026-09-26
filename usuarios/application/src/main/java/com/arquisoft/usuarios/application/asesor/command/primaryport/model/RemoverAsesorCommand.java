package com.arquisoft.usuarios.application.asesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverAsesorCommand(UUID usuario) {

    public static RemoverAsesorCommand crear(UUID usuario) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.Asesor.USUARIO,
                UsuariosCodes.Asesor.USUARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverAsesorCommand(usuario);
    }
}
