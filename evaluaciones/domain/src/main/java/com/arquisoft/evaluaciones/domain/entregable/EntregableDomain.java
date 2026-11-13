package com.arquisoft.evaluaciones.domain.entregable;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class EntregableDomain {

    private UUID id;
    private UUID proyectoGrado;
    private String proyecto;
    private Integer version;

    private EntregableDomain() {}

    public static EntregableDomain crear(UUID id, UUID proyectoGrado, String proyecto, Integer version) {
        var entregable = new EntregableDomain();
        var result = new ValidationResult();

        entregable.setId(id, result);
        entregable.setProyectoGrado(proyectoGrado, result);
        entregable.setProyecto(proyecto, result);
        entregable.setVersion(version, result);

        result.lanzarSiTieneErrores();
        return entregable;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                EvaluacionesFields.Entregable.ID,
                EvaluacionesCodes.Entregable.ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EvaluacionesFields.Entregable.PROYECTO_GRADO,
                EvaluacionesCodes.Entregable.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setProyecto(String proyecto, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(proyecto,
                EvaluacionesFields.Entregable.PROYECTO,
                EvaluacionesCodes.Entregable.PROYECTO_REQUERIDO, result)) {
            return;
        }
        this.proyecto = proyecto;
    }

    private void setVersion(Integer version, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(version,
                EvaluacionesFields.Entregable.VERSION,
                EvaluacionesCodes.Entregable.VERSION_REQUERIDA, result)) {
            return;
        }
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public String getProyecto() {
        return proyecto;
    }

    public Integer getVersion() {
        return version;
    }
}
