package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarItemsFichaPerfilEstudianteQuery(
        UUID fichaPerfil,
        UUID estudiante
) {

    public static ConsultarItemsFichaPerfilEstudianteQuery crear(UUID fichaPerfil, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.ItemFichaPerfil.FICHA_PERFIL,
                FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarItemsFichaPerfilEstudianteQuery(fichaPerfil, estudiante);
    }
}
