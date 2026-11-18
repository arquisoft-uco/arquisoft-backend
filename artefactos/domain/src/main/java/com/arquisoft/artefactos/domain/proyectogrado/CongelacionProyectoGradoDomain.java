package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class CongelacionProyectoGradoDomain {

    private UUID proyectoGrado;
    private Instant entregadoEn;

    private CongelacionProyectoGradoDomain() {}

    public static CongelacionProyectoGradoDomain crear(UUID proyectoGrado, Instant entregadoEn) {
        var congelacion = new CongelacionProyectoGradoDomain();
        var result = new ValidationResult();
        congelacion.setProyectoGrado(proyectoGrado, result);
        congelacion.setEntregadoEn(entregadoEn, result);
        result.lanzarSiTieneErrores();
        return congelacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setEntregadoEn(Instant entregadoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregadoEn,
                ArtefactosFields.ProyectoGrado.ENTREGADO_EN,
                ArtefactosCodes.ProyectoGrado.ENTREGADO_EN_REQUERIDO, result)) {
            return;
        }
        this.entregadoEn = entregadoEn;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public Instant getEntregadoEn() {
        return entregadoEn;
    }
}
