package com.arquisoft.fichas.application.fichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record CambiarAsesorFichaCommand(
        UUID fichaPerfil,
        UUID nuevoAsesorFicha
) {

    public static CambiarAsesorFichaCommand crear(UUID fichaPerfil, String nuevoAsesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.FichaPerfil.ID, FichasCodes.FichaPerfil.ID_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(nuevoAsesorFicha,
                FichasFields.FichaPerfil.ASESOR_FICHA, FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(nuevoAsesorFicha,
                    FichasFields.FichaPerfil.ASESOR_FICHA, FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarAsesorFichaCommand(fichaPerfil, UtilUUID.generarUUIDDesdeTexto(nuevoAsesorFicha));
    }
}
