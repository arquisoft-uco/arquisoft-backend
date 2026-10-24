package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record CambiarNombreRepositorioVersionRepositorioArtefactoCommand(
        UUID repositorioArtefacto,
        String nombreRepositorio
) {

    public static CambiarNombreRepositorioVersionRepositorioArtefactoCommand crear(
            String repositorioArtefacto, String nombreRepositorio) {
        var result = new ValidationResult();
        var nombreRecortado = UtilTexto.aplicarTrim(nombreRepositorio);

        ValidatorUUID.uuidValido(repositorioArtefacto,
                ArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO,
                ArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(nombreRecortado,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(nombreRecortado,
                    ArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_MAX,
                    ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO,
                    ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_DEMASIADO_LARGO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarNombreRepositorioVersionRepositorioArtefactoCommand(
                UtilUUID.generarUUIDDesdeTexto(repositorioArtefacto), nombreRecortado);
    }
}
