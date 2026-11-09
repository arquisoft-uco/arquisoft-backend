package com.arquisoft.artefactos.domain.paqueteentregable;

import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoIncluidoPaquete;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class PaqueteEntregableDomain {

    private static final long TAMANO_MINIMO_BYTES = 1L;
    private static final int VERSION_MINIMA = 1;

    private UUID id;
    private UUID entregableProyectoGrado;
    private UUID proyectoGrado;
    private int versionEntregable;
    private Instant generadoEn;
    private List<ArtefactoIncluidoPaquete> incluidos;
    private String claveObjeto;
    private long tamanoBytes;

    private PaqueteEntregableDomain() {}

    public static PaqueteEntregableDomain crear(
            GeneracionPaqueteEntregableDomain generacion, List<ArtefactoIncluidoPaquete> incluidos,
            String claveObjeto, long tamanoBytes) {
        var paquete = new PaqueteEntregableDomain();
        var result = new ValidationResult();
        paquete.setId();
        if (ValidatorObjeto.noNulo(generacion,
                ArtefactosFields.PaqueteEntregable.GENERACION,
                ArtefactosCodes.PaqueteEntregable.GENERACION_REQUERIDA, result)) {
            paquete.setEntregableProyectoGrado(generacion.getEntregableProyectoGrado(), result);
            paquete.setProyectoGrado(generacion.getProyectoGrado(), result);
            paquete.setVersionEntregable(generacion.getVersionEntregable(), result);
            paquete.setGeneradoEn(generacion.getGeneradoEn(), result);
        }
        paquete.setIncluidos(incluidos, result);
        paquete.setClaveObjeto(claveObjeto, result);
        paquete.setTamanoBytes(tamanoBytes, result);
        result.lanzarSiTieneErrores();
        return paquete;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEntregableProyectoGrado(UUID entregableProyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregableProyectoGrado,
                ArtefactosFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO,
                ArtefactosCodes.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.entregableProyectoGrado = entregableProyectoGrado;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO,
                ArtefactosCodes.PaqueteEntregable.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setVersionEntregable(int versionEntregable, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(versionEntregable, VERSION_MINIMA,
                ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE,
                ArtefactosCodes.PaqueteEntregable.VERSION_ENTREGABLE_INVALIDA, result)) {
            return;
        }
        this.versionEntregable = versionEntregable;
    }

    private void setGeneradoEn(Instant generadoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(generadoEn,
                ArtefactosFields.PaqueteEntregable.GENERADO_EN,
                ArtefactosCodes.PaqueteEntregable.GENERADO_EN_REQUERIDO, result)) {
            return;
        }
        this.generadoEn = generadoEn;
    }

    private void setIncluidos(List<ArtefactoIncluidoPaquete> incluidos, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(incluidos,
                ArtefactosFields.PaqueteEntregable.INCLUIDOS,
                ArtefactosCodes.PaqueteEntregable.INCLUIDOS_REQUERIDOS, result)) {
            return;
        }
        this.incluidos = List.copyOf(incluidos);
    }

    private void setClaveObjeto(String claveObjeto, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(claveObjeto);
        if (!ValidatorTexto.noEnBlanco(recortada,
                ArtefactosFields.PaqueteEntregable.CLAVE_OBJETO,
                ArtefactosCodes.PaqueteEntregable.CLAVE_OBJETO_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada,
                ArtefactosLimits.PaqueteEntregable.CLAVE_OBJETO_MAX,
                ArtefactosFields.PaqueteEntregable.CLAVE_OBJETO,
                ArtefactosCodes.PaqueteEntregable.CLAVE_OBJETO_DEMASIADO_LARGA, result)) {
            return;
        }
        this.claveObjeto = recortada;
    }

    private void setTamanoBytes(long tamanoBytes, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(tamanoBytes, TAMANO_MINIMO_BYTES,
                ArtefactosFields.PaqueteEntregable.TAMANO_BYTES,
                ArtefactosCodes.PaqueteEntregable.TAMANO_BYTES_INVALIDO, result)) {
            return;
        }
        this.tamanoBytes = tamanoBytes;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public int getVersionEntregable() {
        return versionEntregable;
    }

    public Instant getGeneradoEn() {
        return generadoEn;
    }

    public List<ArtefactoIncluidoPaquete> getIncluidos() {
        return incluidos;
    }

    public String getClaveObjeto() {
        return claveObjeto;
    }

    public long getTamanoBytes() {
        return tamanoBytes;
    }
}
