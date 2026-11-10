package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.constant.EntregablesLimits;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AgregarPaqueteEntregableCommand(
        UUID paqueteEntregable,
        UUID entregableProyectoGrado,
        String bucket,
        String claveObjeto,
        String nombreArchivo,
        long tamanoBytes,
        Instant generadoEn,
        List<ArtefactoPaqueteCommand> artefactos
) {

    private static final long TAMANO_MINIMO_BYTES = 1L;

    public AgregarPaqueteEntregableCommand {
        artefactos = UtilColeccion.aplicarPorDefecto(artefactos);
    }

    public static AgregarPaqueteEntregableCommand crear(
            String paqueteEntregable, String entregableProyectoGrado, DatosArchivoPaquete archivo,
            Instant generadoEn, List<DatosArtefactoPaquete> artefactos) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(paqueteEntregable,
                EntregablesFields.PaqueteEntregable.ID,
                EntregablesCodes.PaqueteEntregable.ID_REQUERIDO, result);
        ValidatorUUID.uuidValido(entregableProyectoGrado,
                EntregablesFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO,
                EntregablesCodes.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(archivo.bucket(),
                EntregablesFields.PaqueteEntregable.BUCKET,
                EntregablesCodes.PaqueteEntregable.BUCKET_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(archivo.claveObjeto(),
                EntregablesFields.PaqueteEntregable.CLAVE_OBJETO,
                EntregablesCodes.PaqueteEntregable.CLAVE_OBJETO_REQUERIDA, result);
        ValidatorTexto.noEnBlanco(archivo.nombreArchivo(),
                EntregablesFields.PaqueteEntregable.NOMBRE_ARCHIVO,
                EntregablesCodes.PaqueteEntregable.NOMBRE_ARCHIVO_REQUERIDO, result);
        if (ValidatorObjeto.noNulo(archivo.tamanoBytes(),
                EntregablesFields.PaqueteEntregable.TAMANO_BYTES,
                EntregablesCodes.PaqueteEntregable.TAMANO_BYTES_INVALIDO, result)) {
            ValidatorNumero.valorMinimo(archivo.tamanoBytes(), TAMANO_MINIMO_BYTES,
                    EntregablesFields.PaqueteEntregable.TAMANO_BYTES,
                    EntregablesCodes.PaqueteEntregable.TAMANO_BYTES_INVALIDO, result);
        }
        ValidatorObjeto.noNulo(generadoEn,
                EntregablesFields.PaqueteEntregable.GENERADO_EN,
                EntregablesCodes.PaqueteEntregable.GENERADO_EN_REQUERIDO, result);
        var items = UtilColeccion.aplicarPorDefecto(artefactos);
        items.forEach(item -> validarArtefacto(item, result));
        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarPaqueteEntregableCommand(
                UtilUUID.generarUUIDDesdeTexto(paqueteEntregable),
                UtilUUID.generarUUIDDesdeTexto(entregableProyectoGrado),
                archivo.bucket(), archivo.claveObjeto(), archivo.nombreArchivo(), archivo.tamanoBytes(), generadoEn,
                items.stream().map(AgregarPaqueteEntregableCommand::toArtefacto).toList());
    }

    private static void validarArtefacto(DatosArtefactoPaquete item, ValidationResult result) {
        ValidatorUUID.uuidValido(item.artefacto(),
                EntregablesFields.ArtefactoEntregable.ARTEFACTO,
                EntregablesCodes.ArtefactoEntregable.ARTEFACTO_INVALIDO, result);
        if (ValidatorObjeto.noNulo(item.numeroVersion(),
                EntregablesFields.ArtefactoEntregable.NUMERO_VERSION,
                EntregablesCodes.ArtefactoEntregable.NUMERO_VERSION_INVALIDO, result)) {
            ValidatorNumero.valorMinimo(item.numeroVersion(), EntregablesLimits.ArtefactoEntregable.NUMERO_VERSION_MIN,
                    EntregablesFields.ArtefactoEntregable.NUMERO_VERSION,
                    EntregablesCodes.ArtefactoEntregable.NUMERO_VERSION_INVALIDO, result);
        }
        ValidatorUUID.uuidValido(item.versionRepositorioArtefacto(),
                EntregablesFields.ArtefactoEntregable.VERSION_REPOSITORIO_ARTEFACTO,
                EntregablesCodes.ArtefactoEntregable.VERSION_REPOSITORIO_ARTEFACTO_INVALIDA, result);
        ValidatorUUID.uuidValido(item.repositorio(),
                EntregablesFields.ArtefactoEntregable.REPOSITORIO,
                EntregablesCodes.ArtefactoEntregable.REPOSITORIO_INVALIDO, result);
        validarTexto(item.nombreDocumento(), EntregablesLimits.ArtefactoEntregable.NOMBRE_DOCUMENTO_MAX,
                EntregablesFields.ArtefactoEntregable.NOMBRE_DOCUMENTO,
                EntregablesCodes.ArtefactoEntregable.NOMBRE_DOCUMENTO_INVALIDO, result);
        validarTexto(item.nombreRepositorio(), EntregablesLimits.ArtefactoEntregable.NOMBRE_REPOSITORIO_MAX,
                EntregablesFields.ArtefactoEntregable.NOMBRE_REPOSITORIO,
                EntregablesCodes.ArtefactoEntregable.NOMBRE_REPOSITORIO_INVALIDO, result);
        validarTexto(item.rutaEnPaquete(), EntregablesLimits.ArtefactoEntregable.URI_ARCHIVO_MAX,
                EntregablesFields.ArtefactoEntregable.URI_ARCHIVO,
                EntregablesCodes.ArtefactoEntregable.URI_ARCHIVO_INVALIDA, result);
    }

    private static void validarTexto(String valor, int maximo, String campo, String codigo, ValidationResult result) {
        if (ValidatorTexto.noEnBlanco(valor, campo, codigo, result)) {
            ValidatorLongitud.longitudMaxima(valor, maximo, campo, codigo, result);
        }
    }

    private static ArtefactoPaqueteCommand toArtefacto(DatosArtefactoPaquete item) {
        return new ArtefactoPaqueteCommand(
                UtilUUID.generarUUIDDesdeTexto(item.artefacto()),
                item.numeroVersion(),
                UtilUUID.generarUUIDDesdeTexto(item.versionRepositorioArtefacto()),
                item.nombreDocumento(),
                UtilUUID.generarUUIDDesdeTexto(item.repositorio()),
                item.nombreRepositorio(),
                item.rutaEnPaquete());
    }
}
