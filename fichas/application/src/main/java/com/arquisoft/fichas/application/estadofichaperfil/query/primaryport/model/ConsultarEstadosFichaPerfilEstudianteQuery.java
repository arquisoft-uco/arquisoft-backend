package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEstadosFichaPerfilEstudianteQuery(
        UUID fichaPerfil,
        UUID estudiante
) {

    public static ConsultarEstadosFichaPerfilEstudianteQuery crear(UUID fichaPerfil, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(estudiante,
                FichasFields.EstadoFichaPerfil.ESTUDIANTE,
                FichasCodes.EstadoFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEstadosFichaPerfilEstudianteQuery(fichaPerfil, estudiante);
    }
}
