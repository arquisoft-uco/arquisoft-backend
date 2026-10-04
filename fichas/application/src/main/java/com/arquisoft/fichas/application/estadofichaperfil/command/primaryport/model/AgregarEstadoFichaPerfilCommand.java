package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record AgregarEstadoFichaPerfilCommand(
        UUID fichaPerfil,
        String estadoFicha,
        UUID asesorFicha
) {

    public static AgregarEstadoFichaPerfilCommand crear(UUID fichaPerfil, String estadoFicha, UUID asesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);

        ValidatorTexto.noEnBlanco(estadoFicha,
                FichasFields.EstadoFichaPerfil.ESTADO_FICHA,
                FichasCodes.EstadoFichaPerfil.ESTADO_FICHA_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.EstadoFichaPerfil.ASESOR_FICHA,
                FichasCodes.EstadoFichaPerfil.ASESOR_FICHA_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEstadoFichaPerfilCommand(fichaPerfil, estadoFicha, asesorFicha);
    }
}
