package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarItemsFichaPerfilAsesorQuery(
        UUID fichaPerfil,
        UUID asesorFicha
) {

    public static ConsultarItemsFichaPerfilAsesorQuery crear(UUID fichaPerfil, UUID asesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.ItemFichaPerfil.FICHA_PERFIL,
                FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.ItemFichaPerfil.ASESOR_FICHA,
                FichasCodes.ItemFichaPerfil.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarItemsFichaPerfilAsesorQuery(fichaPerfil, asesorFicha);
    }
}
