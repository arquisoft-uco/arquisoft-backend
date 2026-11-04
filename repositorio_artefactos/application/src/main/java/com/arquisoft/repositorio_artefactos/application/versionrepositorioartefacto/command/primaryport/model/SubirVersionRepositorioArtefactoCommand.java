package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model;

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

public record SubirVersionRepositorioArtefactoCommand(
        UUID carga,
        UUID repositorio,
        UUID solicitante,
        String nombreDocumento,
        Boolean nuevoDocumento
) {
    public SubirVersionRepositorioArtefactoCommand {
        nombreDocumento = UtilTexto.aplicarTrim(nombreDocumento);
    }

    public static SubirVersionRepositorioArtefactoCommand crear(
            String carga, UUID repositorio, UUID solicitante, String nombreDocumento, Boolean nuevoDocumento) {
        var result = new ValidationResult();
        var documentoRecortado = UtilTexto.aplicarTrim(nombreDocumento);

        ValidatorUUID.uuidValido(carga,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.CARGA,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.CARGA_REQUERIDA, result);

        ValidatorObjeto.noNulo(repositorio,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_REQUERIDO, result);

        ValidatorObjeto.noNulo(solicitante,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.SOLICITANTE,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.SOLICITANTE_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(documentoRecortado,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(documentoRecortado,
                    RepositorioArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_MAX,
                    RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                    RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_DEMASIADO_LARGO, result);
        }

        ValidatorObjeto.noNulo(nuevoDocumento,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.NUEVO_DOCUMENTO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.NUEVO_DOCUMENTO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new SubirVersionRepositorioArtefactoCommand(
                UtilUUID.generarUUIDDesdeTexto(carga), repositorio, solicitante, documentoRecortado, nuevoDocumento);
    }
}
