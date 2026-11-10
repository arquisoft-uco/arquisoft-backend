package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class FinalizacionProyectoGradoDomain {

    private UUID proyectoGrado;
    private Instant finalizadoEn;

    private FinalizacionProyectoGradoDomain() {}

    public static FinalizacionProyectoGradoDomain crear(UUID proyectoGrado, Instant finalizadoEn) {
        var finalizacion = new FinalizacionProyectoGradoDomain();
        var result = new ValidationResult();
        finalizacion.setProyectoGrado(proyectoGrado, result);
        finalizacion.setFinalizadoEn(finalizadoEn, result);
        result.lanzarSiTieneErrores();
        return finalizacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setFinalizadoEn(Instant finalizadoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(finalizadoEn,
                ArtefactosFields.ProyectoGrado.FINALIZADO_EN,
                ArtefactosCodes.ProyectoGrado.FINALIZADO_EN_REQUERIDO, result)) {
            return;
        }
        this.finalizadoEn = finalizadoEn;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public Instant getFinalizadoEn() {
        return finalizadoEn;
    }
}
