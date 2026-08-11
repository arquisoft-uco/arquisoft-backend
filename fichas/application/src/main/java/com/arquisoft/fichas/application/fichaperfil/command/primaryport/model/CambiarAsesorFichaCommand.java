package com.arquisoft.fichas.application.fichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public record CambiarAsesorFichaCommand(
        UUID fichaPerfil,
        UUID nuevoAsesorFicha
) {

    public static CambiarAsesorFichaCommand crear(UUID fichaPerfil, String nuevoAsesorFicha) {
        var result = new ValidationResult();

        DomainValidator.noNulo(fichaPerfil,
                FichasFields.FichaPerfil.ID, FichasCodes.FichaPerfil.ID_REQUERIDO, result);

        if (DomainValidator.noEnBlanco(nuevoAsesorFicha,
                FichasFields.FichaPerfil.ASESOR_FICHA, FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result)) {
            DomainValidator.uuidValido(nuevoAsesorFicha,
                    FichasFields.FichaPerfil.ASESOR_FICHA, FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarAsesorFichaCommand(fichaPerfil, UtilUUID.generateUUIDFromString(nuevoAsesorFicha));
    }
}
