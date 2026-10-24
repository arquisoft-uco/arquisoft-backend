package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.message.constant.RepositorioArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record GenerarRepositorioArtefactoCommand(
        String nombreRepositorio,
        String descripcion,
        UUID administrador
) {
    public GenerarRepositorioArtefactoCommand {
        nombreRepositorio = UtilTexto.aplicarTrim(nombreRepositorio);
        descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public static GenerarRepositorioArtefactoCommand crear(
            String nombreRepositorio, String descripcion, UUID administrador) {
        var result = new ValidationResult();
        var nombreRecortado = UtilTexto.aplicarTrim(nombreRepositorio);
        var descripcionRecortada = UtilTexto.aplicarTrim(descripcion);

        if (ValidatorTexto.noEnBlanco(nombreRecortado,
                RepositorioArtefactosFields.RepositorioArtefacto.NOMBRE_REPOSITORIO,
                RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(nombreRecortado,
                    RepositorioArtefactosLimits.RepositorioArtefacto.NOMBRE_MAX,
                    RepositorioArtefactosFields.RepositorioArtefacto.NOMBRE_REPOSITORIO,
                    RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_DEMASIADO_LARGO, result);
        }

        if (ValidatorTexto.noEnBlanco(descripcionRecortada,
                RepositorioArtefactosFields.RepositorioArtefacto.DESCRIPCION,
                RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(descripcionRecortada,
                    RepositorioArtefactosLimits.RepositorioArtefacto.DESCRIPCION_MAX,
                    RepositorioArtefactosFields.RepositorioArtefacto.DESCRIPCION,
                    RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_DEMASIADO_LARGA, result);
        }

        ValidatorObjeto.noNulo(administrador,
                RepositorioArtefactosFields.RepositorioArtefacto.ADMINISTRADOR,
                RepositorioArtefactosCodes.RepositorioArtefacto.ADMINISTRADOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new GenerarRepositorioArtefactoCommand(
                nombreRecortado, descripcionRecortada, administrador);
    }
}
