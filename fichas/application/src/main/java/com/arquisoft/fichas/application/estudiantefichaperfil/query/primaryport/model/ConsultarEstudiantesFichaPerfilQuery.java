package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEstudiantesFichaPerfilQuery(
        UUID fichaPerfil
) {

    public static ConsultarEstudiantesFichaPerfilQuery crear(UUID fichaPerfil) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EstudianteFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEstudiantesFichaPerfilQuery(fichaPerfil);
    }
}
