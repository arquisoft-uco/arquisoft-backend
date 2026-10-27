package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record SolicitarCargaVersionArtefactoCommand(
        UUID artefacto,
        UUID solicitante,
        String nombreArchivo,
        String tipoContenido,
        Long tamanoBytes
) {
    public SolicitarCargaVersionArtefactoCommand {
        nombreArchivo = UtilTexto.aplicarTrim(nombreArchivo);
        tipoContenido = UtilTexto.aplicarTrim(tipoContenido);
    }

    public static SolicitarCargaVersionArtefactoCommand crear(
            UUID artefacto, UUID solicitante, String nombreArchivo, String tipoContenido, Long tamanoBytes) {
        var result = new ValidationResult();
        var nombreRecortado = UtilTexto.aplicarTrim(nombreArchivo);
        var tipoRecortado = UtilTexto.aplicarTrim(tipoContenido);

        ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.VersionArtefacto.ARTEFACTO,
                ArtefactosCodes.VersionArtefacto.ARTEFACTO_REQUERIDO, result);

        ValidatorObjeto.noNulo(solicitante,
                ArtefactosFields.VersionArtefacto.SOLICITANTE,
                ArtefactosCodes.VersionArtefacto.SOLICITANTE_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(nombreRecortado,
                ArtefactosFields.VersionArtefacto.NOMBRE_ARCHIVO,
                ArtefactosCodes.VersionArtefacto.NOMBRE_ARCHIVO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(nombreRecortado,
                    ArtefactosLimits.VersionArtefacto.NOMBRE_ARCHIVO_MAX,
                    ArtefactosFields.VersionArtefacto.NOMBRE_ARCHIVO,
                    ArtefactosCodes.VersionArtefacto.NOMBRE_ARCHIVO_DEMASIADO_LARGO, result);
        }

        if (ValidatorTexto.noEnBlanco(tipoRecortado,
                ArtefactosFields.VersionArtefacto.TIPO_CONTENIDO,
                ArtefactosCodes.VersionArtefacto.TIPO_CONTENIDO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(tipoRecortado,
                    ArtefactosLimits.VersionArtefacto.TIPO_CONTENIDO_MAX,
                    ArtefactosFields.VersionArtefacto.TIPO_CONTENIDO,
                    ArtefactosCodes.VersionArtefacto.TIPO_CONTENIDO_DEMASIADO_LARGO, result);
        }

        if (ValidatorObjeto.noNulo(tamanoBytes,
                ArtefactosFields.VersionArtefacto.TAMANO_BYTES,
                ArtefactosCodes.VersionArtefacto.TAMANO_BYTES_REQUERIDO, result)) {
            ValidatorNumero.valorMinimo(tamanoBytes, 1,
                    ArtefactosFields.VersionArtefacto.TAMANO_BYTES,
                    ArtefactosCodes.VersionArtefacto.TAMANO_BYTES_INVALIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new SolicitarCargaVersionArtefactoCommand(
                artefacto, solicitante, nombreRecortado, tipoRecortado, tamanoBytes);
    }
}
