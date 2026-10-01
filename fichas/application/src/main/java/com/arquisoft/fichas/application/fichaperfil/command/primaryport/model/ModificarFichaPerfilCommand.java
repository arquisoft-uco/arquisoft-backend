package com.arquisoft.fichas.application.fichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ModificarFichaPerfilCommand(
        UUID fichaPerfil,
        UUID estudiante,
        String tituloProyecto
) {

    public ModificarFichaPerfilCommand {
        tituloProyecto = UtilTexto.aplicarTrim(tituloProyecto);
    }

    public static ModificarFichaPerfilCommand crear(UUID fichaPerfil, UUID estudiante, String tituloProyecto) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.FichaPerfil.ID, FichasCodes.FichaPerfil.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                FichasFields.FichaPerfil.ESTUDIANTE, FichasCodes.FichaPerfil.ESTUDIANTE_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(tituloProyecto,
                FichasFields.FichaPerfil.TITULO, FichasCodes.FichaPerfil.TITULO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(tituloProyecto, FichasLimits.FichaPerfil.TITULO_MAX,
                    FichasFields.FichaPerfil.TITULO, FichasCodes.FichaPerfil.TITULO_DEMASIADO_LARGO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarFichaPerfilCommand(fichaPerfil, estudiante, tituloProyecto);
    }
}
