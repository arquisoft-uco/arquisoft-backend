package com.arquisoft.fichas.application.fichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

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

        DomainValidator.noNulo(fichaPerfil,
                FichasFields.FichaPerfil.ID, FichasCodes.FichaPerfil.ID_REQUERIDO, result);
        DomainValidator.noNulo(estudiante,
                FichasFields.FichaPerfil.ESTUDIANTE, FichasCodes.FichaPerfil.ESTUDIANTE_REQUERIDO, result);

        if (DomainValidator.noEnBlanco(tituloProyecto,
                FichasFields.FichaPerfil.TITULO, FichasCodes.FichaPerfil.TITULO_REQUERIDO, result)) {
            DomainValidator.longitudMaxima(tituloProyecto, FichasLimits.FichaPerfil.TITULO_MAX,
                    FichasFields.FichaPerfil.TITULO, FichasCodes.FichaPerfil.TITULO_DEMASIADO_LARGO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarFichaPerfilCommand(fichaPerfil, estudiante, tituloProyecto);
    }
}
