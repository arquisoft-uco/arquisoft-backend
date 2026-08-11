package com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public record RegistrarEvaluacionFichaPerfilCommand(
        UUID fichaPerfil,
        UUID representanteComite
) {

    public static RegistrarEvaluacionFichaPerfilCommand crear(UUID fichaPerfil, UUID representanteComite) {
        var result = new ValidationResult();

        DomainValidator.noNulo(fichaPerfil,
                FichasFields.EvaluacionFichaPerfil.FICHA_PERFIL,
                FichasCodes.EvaluacionFichaPerfil.FICHA_REQUERIDA, result);
        DomainValidator.noNulo(representanteComite,
                FichasFields.EvaluacionFichaPerfil.REPRESENTANTE_COMITE,
                FichasCodes.EvaluacionFichaPerfil.REPRESENTANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarEvaluacionFichaPerfilCommand(fichaPerfil, representanteComite);
    }
}
