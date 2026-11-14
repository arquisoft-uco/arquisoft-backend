package com.arquisoft.entregables.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record RegistrarVersionCerradaArtefactoCommand(
        UUID artefacto,
        UUID proyectoGrado,
        String documento,
        UUID versionArtefacto,
        int numeroVersion,
        String uriArchivo,
        Instant fechaVersion,
        UUID versionRepositorioArtefacto,
        UUID repositorio,
        String nombreRepositorio
) {

    public record DatosVersion(String versionArtefacto, Integer numeroVersion, String uriArchivo, Instant fechaVersion) {}

    public record DatosRepositorio(String versionRepositorioArtefacto, String repositorio, String nombreRepositorio) {}

    public static RegistrarVersionCerradaArtefactoCommand crear(
            String artefacto, String proyectoGrado, String documento, DatosVersion version,
            DatosRepositorio datosRepositorio) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(artefacto,
                EntregablesFields.Artefacto.ID,
                EntregablesCodes.Artefacto.ID_REQUERIDO, result);
        ValidatorUUID.uuidValido(proyectoGrado,
                EntregablesFields.Artefacto.PROYECTO_GRADO,
                EntregablesCodes.Artefacto.PROYECTO_GRADO_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(documento,
                EntregablesFields.Artefacto.NOMBRE_DOCUMENTO,
                EntregablesCodes.Artefacto.NOMBRE_DOCUMENTO_REQUERIDO, result);
        ValidatorUUID.uuidValido(version.versionArtefacto(),
                EntregablesFields.Artefacto.VERSION_ARTEFACTO,
                EntregablesCodes.Artefacto.VERSION_ARTEFACTO_INVALIDA, result);
        ValidatorObjeto.noNulo(version.numeroVersion(),
                EntregablesFields.Artefacto.NUMERO_VERSION,
                EntregablesCodes.Artefacto.NUMERO_VERSION_INVALIDO, result);
        ValidatorTexto.noEnBlanco(version.uriArchivo(),
                EntregablesFields.Artefacto.CLAVE_ORIGEN,
                EntregablesCodes.Artefacto.CLAVE_ORIGEN_REQUERIDA, result);
        ValidatorObjeto.noNulo(version.fechaVersion(),
                EntregablesFields.Artefacto.FECHA_VERSION,
                EntregablesCodes.Artefacto.FECHA_VERSION_REQUERIDA, result);
        ValidatorUUID.uuidValido(datosRepositorio.versionRepositorioArtefacto(),
                EntregablesFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO,
                EntregablesCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_INVALIDA, result);
        ValidatorUUID.uuidValido(datosRepositorio.repositorio(),
                EntregablesFields.Artefacto.REPOSITORIO,
                EntregablesCodes.Artefacto.REPOSITORIO_INVALIDO, result);
        ValidatorTexto.noEnBlanco(datosRepositorio.nombreRepositorio(),
                EntregablesFields.Artefacto.NOMBRE_REPOSITORIO,
                EntregablesCodes.Artefacto.NOMBRE_REPOSITORIO_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarVersionCerradaArtefactoCommand(
                UtilUUID.generarUUIDDesdeTexto(artefacto),
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                documento,
                UtilUUID.generarUUIDDesdeTexto(version.versionArtefacto()),
                UtilNumero.obtenerPorDefecto(version.numeroVersion()),
                version.uriArchivo(),
                version.fechaVersion(),
                UtilUUID.generarUUIDDesdeTexto(datosRepositorio.versionRepositorioArtefacto()),
                UtilUUID.generarUUIDDesdeTexto(datosRepositorio.repositorio()),
                datosRepositorio.nombreRepositorio());
    }
}
