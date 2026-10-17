package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.message.constant.RepositorioArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record SolicitarCargaVersionRepositorioArtefactoCommand(
        UUID repositorio,
        UUID solicitante,
        String nombreArchivo,
        String tipoContenido,
        Long tamanoBytes
) {
    public SolicitarCargaVersionRepositorioArtefactoCommand {
        nombreArchivo = UtilTexto.aplicarTrim(nombreArchivo);
        tipoContenido = UtilTexto.aplicarTrim(tipoContenido);
    }

    public static SolicitarCargaVersionRepositorioArtefactoCommand crear(
            UUID repositorio, String solicitante, String nombreArchivo, String tipoContenido, Long tamanoBytes) {
        var result = new ValidationResult();
        var nombreRecortado = UtilTexto.aplicarTrim(nombreArchivo);
        var tipoRecortado = UtilTexto.aplicarTrim(tipoContenido);

        ValidatorObjeto.noNulo(repositorio,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_REQUERIDO, result);

        ValidatorUUID.uuidValido(solicitante,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.SOLICITANTE,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.SOLICITANTE_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(nombreRecortado,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(nombreRecortado,
                    RepositorioArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_MAX,
                    RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                    RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_DEMASIADO_LARGO, result);
        }

        if (ValidatorTexto.noEnBlanco(tipoRecortado,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(tipoRecortado,
                    RepositorioArtefactosLimits.VersionRepositorioArtefacto.TIPO_CONTENIDO_MAX,
                    RepositorioArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                    RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_DEMASIADO_LARGO, result);
        }

        if (ValidatorObjeto.noNulo(tamanoBytes,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_REQUERIDO, result)) {
            ValidatorNumero.valorMinimo(tamanoBytes, 1,
                    RepositorioArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES,
                    RepositorioArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_INVALIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new SolicitarCargaVersionRepositorioArtefactoCommand(
                repositorio, UtilUUID.generarUUIDDesdeTexto(solicitante), nombreRecortado, tipoRecortado, tamanoBytes);
    }
}
