package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record AgregarPaqueteEntregableCommand(
        UUID paqueteEntregable,
        UUID entregableProyectoGrado,
        String bucket,
        String claveObjeto,
        String nombreArchivo,
        long tamanoBytes,
        Instant generadoEn
) {

    private static final long TAMANO_MINIMO_BYTES = 1L;

    public static AgregarPaqueteEntregableCommand crear(
            String paqueteEntregable, String entregableProyectoGrado, String bucket, String claveObjeto,
            String nombreArchivo, Long tamanoBytes, Instant generadoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(paqueteEntregable,
                EntregablesFields.PaqueteEntregable.ID,
                EntregablesCodes.PaqueteEntregable.ID_REQUERIDO, result);
        ValidatorUUID.uuidValido(entregableProyectoGrado,
                EntregablesFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO,
                EntregablesCodes.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(bucket,
                EntregablesFields.PaqueteEntregable.BUCKET,
                EntregablesCodes.PaqueteEntregable.BUCKET_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(claveObjeto,
                EntregablesFields.PaqueteEntregable.CLAVE_OBJETO,
                EntregablesCodes.PaqueteEntregable.CLAVE_OBJETO_REQUERIDA, result);
        ValidatorTexto.noEnBlanco(nombreArchivo,
                EntregablesFields.PaqueteEntregable.NOMBRE_ARCHIVO,
                EntregablesCodes.PaqueteEntregable.NOMBRE_ARCHIVO_REQUERIDO, result);
        if (ValidatorObjeto.noNulo(tamanoBytes,
                EntregablesFields.PaqueteEntregable.TAMANO_BYTES,
                EntregablesCodes.PaqueteEntregable.TAMANO_BYTES_INVALIDO, result)) {
            ValidatorNumero.valorMinimo(tamanoBytes, TAMANO_MINIMO_BYTES,
                    EntregablesFields.PaqueteEntregable.TAMANO_BYTES,
                    EntregablesCodes.PaqueteEntregable.TAMANO_BYTES_INVALIDO, result);
        }
        ValidatorObjeto.noNulo(generadoEn,
                EntregablesFields.PaqueteEntregable.GENERADO_EN,
                EntregablesCodes.PaqueteEntregable.GENERADO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarPaqueteEntregableCommand(
                UtilUUID.generarUUIDDesdeTexto(paqueteEntregable),
                UtilUUID.generarUUIDDesdeTexto(entregableProyectoGrado),
                bucket, claveObjeto, nombreArchivo, tamanoBytes, generadoEn);
    }
}
