package com.arquisoft.artefactos.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class GeneracionPaqueteEntregableDomain {

    private static final int VERSION_MINIMA = 1;

    private UUID entregableProyectoGrado;
    private UUID proyectoGrado;
    private int versionEntregable;
    private Instant generadoEn;

    private GeneracionPaqueteEntregableDomain() {}

    public static GeneracionPaqueteEntregableDomain crear(
            UUID entregableProyectoGrado, UUID proyectoGrado, Integer versionEntregable) {
        var generacion = new GeneracionPaqueteEntregableDomain();
        var result = new ValidationResult();
        generacion.setEntregableProyectoGrado(entregableProyectoGrado, result);
        generacion.setProyectoGrado(proyectoGrado, result);
        generacion.setVersionEntregable(versionEntregable, result);
        generacion.setGeneradoEn();
        result.lanzarSiTieneErrores();
        return generacion;
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

    private void setVersionEntregable(Integer versionEntregable, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(versionEntregable,
                ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE,
                ArtefactosCodes.PaqueteEntregable.VERSION_ENTREGABLE_INVALIDA, result)) {
            return;
        }
        if (!ValidatorNumero.valorMinimo(versionEntregable, VERSION_MINIMA,
                ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE,
                ArtefactosCodes.PaqueteEntregable.VERSION_ENTREGABLE_INVALIDA, result)) {
            return;
        }
        this.versionEntregable = versionEntregable;
    }

    private void setGeneradoEn() {
        this.generadoEn = UtilFecha.generarInstanteActual();
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
}
