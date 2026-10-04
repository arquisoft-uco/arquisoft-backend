package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AgregarEstadoAprobacionFichaPerfilCommand(
        UUID fichaPerfil,
        Boolean acepta,
        UUID coordinador
) {

    public static AgregarEstadoAprobacionFichaPerfilCommand crear(String fichaPerfil, Boolean acepta,
                                                                  UUID coordinador) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(fichaPerfil,
                FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(fichaPerfil,
                    FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                    FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);
        }

        ValidatorObjeto.noNulo(acepta,
                FichasFields.EstadoFichaPerfil.ACEPTA,
                FichasCodes.EstadoFichaPerfil.ACEPTA_REQUERIDO, result);

        ValidatorObjeto.noNulo(coordinador,
                FichasFields.EstadoFichaPerfil.COORDINADOR,
                FichasCodes.EstadoFichaPerfil.COORDINADOR_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEstadoAprobacionFichaPerfilCommand(
                UtilUUID.generarUUIDDesdeTexto(fichaPerfil), acepta, coordinador);
    }
}
