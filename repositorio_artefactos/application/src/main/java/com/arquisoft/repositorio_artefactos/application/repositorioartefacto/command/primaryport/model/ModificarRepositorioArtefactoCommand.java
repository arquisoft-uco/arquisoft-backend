package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.message.constant.RepositorioArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record ModificarRepositorioArtefactoCommand(
        UUID repositorio,
        String nombreRepositorio,
        String descripcion,
        UUID administrador
) {

    public static ModificarRepositorioArtefactoCommand crear(
            UUID repositorio, String nombreRepositorio, String descripcion, String administrador) {
        var result = new ValidationResult();
        var nombreRecortado = UtilTexto.aplicarTrim(nombreRepositorio);
        var descripcionRecortada = UtilTexto.aplicarTrim(descripcion);

        ValidatorObjeto.noNulo(repositorio,
                RepositorioArtefactosFields.RepositorioArtefacto.ID,
                RepositorioArtefactosCodes.RepositorioArtefacto.ID_REQUERIDO, result);

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

        ValidatorUUID.uuidValido(administrador,
                RepositorioArtefactosFields.RepositorioArtefacto.ADMINISTRADOR,
                RepositorioArtefactosCodes.RepositorioArtefacto.ADMINISTRADOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarRepositorioArtefactoCommand(
                repositorio, nombreRecortado, descripcionRecortada, UtilUUID.generarUUIDDesdeTexto(administrador));
    }
}
