package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarItemsFichaPerfilRepresentanteQuery(
        UUID fichaPerfil,
        UUID representanteComite
) {

    public static ConsultarItemsFichaPerfilRepresentanteQuery crear(UUID fichaPerfil, UUID representanteComite) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.ItemFichaPerfil.FICHA_PERFIL,
                FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(representanteComite,
                FichasFields.ItemFichaPerfil.REPRESENTANTE_COMITE,
                FichasCodes.ItemFichaPerfil.REPRESENTANTE_COMITE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarItemsFichaPerfilRepresentanteQuery(fichaPerfil, representanteComite);
    }
}
