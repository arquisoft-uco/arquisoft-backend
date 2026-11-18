package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class DescongelacionProyectoGradoDomain {

    private UUID proyectoGrado;
    private Instant canceladoEn;

    private DescongelacionProyectoGradoDomain() {}

    public static DescongelacionProyectoGradoDomain crear(UUID proyectoGrado, Instant canceladoEn) {
        var descongelacion = new DescongelacionProyectoGradoDomain();
        var result = new ValidationResult();
        descongelacion.setProyectoGrado(proyectoGrado, result);
        descongelacion.setCanceladoEn(canceladoEn, result);
        result.lanzarSiTieneErrores();
        return descongelacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setCanceladoEn(Instant canceladoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(canceladoEn,
                ArtefactosFields.ProyectoGrado.CANCELADO_EN,
                ArtefactosCodes.ProyectoGrado.CANCELADO_EN_REQUERIDO, result)) {
            return;
        }
        this.canceladoEn = canceladoEn;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public Instant getCanceladoEn() {
        return canceladoEn;
    }
}
