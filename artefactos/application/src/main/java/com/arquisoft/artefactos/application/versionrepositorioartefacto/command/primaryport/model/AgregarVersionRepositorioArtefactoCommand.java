package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AgregarVersionRepositorioArtefactoCommand(
        UUID id,
        String nombreDocumento,
        Integer versionDocumento,
        UUID repositorioArtefacto,
        String nombreRepositorio,
        DatosArchivo archivo
) {

    public record DatosArchivo(String nombreArchivo, String tipoContenido, Long tamanoBytes) {}

    public static AgregarVersionRepositorioArtefactoCommand crear(
            String id, Integer versionDocumento, String nombreDocumento, String repositorioArtefacto,
            String nombreRepositorio, DatosArchivo archivo) {
        var result = new ValidationResult();
        var documentoRecortado = UtilTexto.aplicarTrim(nombreDocumento);
        var repositorioRecortado = UtilTexto.aplicarTrim(nombreRepositorio);
        var nombreArchivoRecortado = UtilTexto.aplicarTrim(archivo.nombreArchivo());
        var tipoContenidoRecortado = UtilTexto.aplicarTrim(archivo.tipoContenido());

        ValidatorUUID.uuidValido(id,
                ArtefactosFields.VersionRepositorioArtefacto.ID,
                ArtefactosCodes.VersionRepositorioArtefacto.ID_REQUERIDO, result);

        validarTexto(documentoRecortado, ArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_MAX,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_REQUERIDO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_DEMASIADO_LARGO, result);

        validarPositivo(versionDocumento,
                ArtefactosFields.VersionRepositorioArtefacto.VERSION_DOCUMENTO,
                ArtefactosCodes.VersionRepositorioArtefacto.VERSION_DOCUMENTO_REQUERIDA,
                ArtefactosCodes.VersionRepositorioArtefacto.VERSION_DOCUMENTO_INVALIDA, result);

        ValidatorUUID.uuidValido(repositorioArtefacto,
                ArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO,
                ArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO_REQUERIDO, result);

        validarTexto(repositorioRecortado, ArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_MAX,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_REQUERIDO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_DEMASIADO_LARGO, result);

        validarTexto(nombreArchivoRecortado, ArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_MAX,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_REQUERIDO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_DEMASIADO_LARGO, result);

        validarTexto(tipoContenidoRecortado, ArtefactosLimits.VersionRepositorioArtefacto.TIPO_CONTENIDO_MAX,
                ArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                ArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_REQUERIDO,
                ArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_DEMASIADO_LARGO, result);

        validarPositivo(archivo.tamanoBytes(),
                ArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES,
                ArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_REQUERIDO,
                ArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_INVALIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarVersionRepositorioArtefactoCommand(
                UtilUUID.generarUUIDDesdeTexto(id),
                documentoRecortado,
                versionDocumento,
                UtilUUID.generarUUIDDesdeTexto(repositorioArtefacto),
                repositorioRecortado,
                new DatosArchivo(nombreArchivoRecortado, tipoContenidoRecortado, archivo.tamanoBytes()));
    }

    private static void validarPositivo(
            Number valor, String campo, String codigoRequerido, String codigoInvalido, ValidationResult result) {
        if (ValidatorObjeto.noNulo(valor, campo, codigoRequerido, result)) {
            ValidatorNumero.valorMinimo(valor, 1, campo, codigoInvalido, result);
        }
    }

    private static void validarTexto(
            String valor, int max, String campo, String codigoRequerido, String codigoLargo,
            ValidationResult result) {
        if (ValidatorTexto.noEnBlanco(valor, campo, codigoRequerido, result)) {
            ValidatorLongitud.longitudMaxima(valor, max, campo, codigoLargo, result);
        }
    }
}
