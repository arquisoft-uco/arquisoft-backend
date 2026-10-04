package com.arquisoft.usuarios.application.bibliotecario.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverBibliotecarioCommand(UUID usuario) {

    public static RemoverBibliotecarioCommand crear(UUID usuario) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.Bibliotecario.USUARIO,
                UsuariosCodes.Bibliotecario.USUARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverBibliotecarioCommand(usuario);
    }
}
