package com.arquisoft.entregables.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class PaqueteEntregableDomain {

    public static final PaqueteEntregableDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO,
            UtilTexto.VACIO, UtilNumero.CERO, UtilFecha.VACIO);

    private static final long TAMANO_MINIMO_BYTES = 1L;

    private UUID id;
    private UUID entregableProyectoGrado;
    private String bucket;
    private String claveObjeto;
    private String nombreArchivo;
    private long tamanoBytes;
    private Instant generadoEn;

    private PaqueteEntregableDomain() {}

    public static PaqueteEntregableDomain crear(UUID entregableProyectoGrado, String bucket, String claveObjeto,
                                                String nombreArchivo, Long tamanoBytes, Instant generadoEn) {
        var paquete = new PaqueteEntregableDomain();
        var result = new ValidationResult();
        paquete.setId();
        paquete.setEntregableProyectoGrado(entregableProyectoGrado, result);
        paquete.setBucket(bucket, result);
        paquete.setClaveObjeto(claveObjeto, result);
        paquete.setNombreArchivo(nombreArchivo, result);
        paquete.setTamanoBytes(tamanoBytes, result);
        paquete.setGeneradoEn(generadoEn, result);
        result.lanzarSiTieneErrores();
        return paquete;
    }

    public static PaqueteEntregableDomain reconstruir(UUID id, UUID entregableProyectoGrado, String bucket,
                                                      String claveObjeto, String nombreArchivo, long tamanoBytes,
                                                      Instant generadoEn) {
        var paquete = new PaqueteEntregableDomain();
        paquete.id = id;
        paquete.entregableProyectoGrado = entregableProyectoGrado;
        paquete.bucket = bucket;
        paquete.claveObjeto = claveObjeto;
        paquete.nombreArchivo = nombreArchivo;
        paquete.tamanoBytes = tamanoBytes;
        paquete.generadoEn = generadoEn;
        return paquete;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEntregableProyectoGrado(UUID entregableProyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregableProyectoGrado,
                EntregablesFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO,
                EntregablesCodes.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.entregableProyectoGrado = entregableProyectoGrado;
    }

    private void setBucket(String bucket, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(bucket,
                EntregablesFields.PaqueteEntregable.BUCKET,
                EntregablesCodes.PaqueteEntregable.BUCKET_REQUERIDO, result)) {
            return;
        }
        this.bucket = bucket;
    }

    private void setClaveObjeto(String claveObjeto, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(claveObjeto,
                EntregablesFields.PaqueteEntregable.CLAVE_OBJETO,
                EntregablesCodes.PaqueteEntregable.CLAVE_OBJETO_REQUERIDA, result)) {
            return;
        }
        this.claveObjeto = claveObjeto;
    }

    private void setNombreArchivo(String nombreArchivo, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombreArchivo,
                EntregablesFields.PaqueteEntregable.NOMBRE_ARCHIVO,
                EntregablesCodes.PaqueteEntregable.NOMBRE_ARCHIVO_REQUERIDO, result)) {
            return;
        }
        this.nombreArchivo = nombreArchivo;
    }

    private void setTamanoBytes(Long tamanoBytes, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tamanoBytes,
                EntregablesFields.PaqueteEntregable.TAMANO_BYTES,
                EntregablesCodes.PaqueteEntregable.TAMANO_BYTES_INVALIDO, result)) {
            return;
        }
        if (!ValidatorNumero.valorMinimo(tamanoBytes, TAMANO_MINIMO_BYTES,
                EntregablesFields.PaqueteEntregable.TAMANO_BYTES,
                EntregablesCodes.PaqueteEntregable.TAMANO_BYTES_INVALIDO, result)) {
            return;
        }
        this.tamanoBytes = tamanoBytes;
    }

    private void setGeneradoEn(Instant generadoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(generadoEn,
                EntregablesFields.PaqueteEntregable.GENERADO_EN,
                EntregablesCodes.PaqueteEntregable.GENERADO_EN_REQUERIDO, result)) {
            return;
        }
        this.generadoEn = generadoEn;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public String getBucket() {
        return bucket;
    }

    public String getClaveObjeto() {
        return claveObjeto;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public long getTamanoBytes() {
        return tamanoBytes;
    }

    public Instant getGeneradoEn() {
        return generadoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
