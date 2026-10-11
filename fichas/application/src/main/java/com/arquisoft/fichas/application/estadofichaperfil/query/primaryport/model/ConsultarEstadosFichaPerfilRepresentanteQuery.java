package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEstadosFichaPerfilRepresentanteQuery(
        UUID fichaPerfil,
        UUID representanteComite
) {

    public static ConsultarEstadosFichaPerfilRepresentanteQuery crear(UUID fichaPerfil, UUID representanteComite) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(representanteComite,
                FichasFields.EstadoFichaPerfil.REPRESENTANTE_COMITE,
                FichasCodes.EstadoFichaPerfil.REPRESENTANTE_COMITE_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEstadosFichaPerfilRepresentanteQuery(fichaPerfil, representanteComite);
    }
}
