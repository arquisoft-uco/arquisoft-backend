package com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverEstudianteFichaPerfilCommand(
        UUID fichaPerfil,
        UUID estudiante
) {

    public static RemoverEstudianteFichaPerfilCommand crear(UUID fichaPerfil, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EstudianteFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                FichasFields.EstudianteFichaPerfil.ESTUDIANTE,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverEstudianteFichaPerfilCommand(fichaPerfil, estudiante);
    }
}
