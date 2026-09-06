package com.arquisoft.fichas.application.fichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarFichaPerfilEstudianteQuery(
        UUID fichaPerfil,
        UUID estudiante
) {

    public static ConsultarFichaPerfilEstudianteQuery crear(UUID fichaPerfil, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.FichaPerfil.ID,
                FichasCodes.FichaPerfil.ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(estudiante,
                FichasFields.FichaPerfil.ESTUDIANTE,
                FichasCodes.FichaPerfil.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarFichaPerfilEstudianteQuery(fichaPerfil, estudiante);
    }
}
