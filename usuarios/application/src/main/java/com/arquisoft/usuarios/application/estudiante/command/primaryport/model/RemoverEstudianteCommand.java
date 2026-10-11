package com.arquisoft.usuarios.application.estudiante.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverEstudianteCommand(UUID usuario) {

    public static RemoverEstudianteCommand crear(UUID usuario) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.Estudiante.USUARIO,
                UsuariosCodes.Estudiante.USUARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverEstudianteCommand(usuario);
    }
}
