package com.arquisoft.entregables.domain.entregableproyectogrado;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class GeneracionEntregableProyectoGradoDomain {

    private UUID proyectoGrado;
    private UUID asesor;

    private GeneracionEntregableProyectoGradoDomain() {}

    public static GeneracionEntregableProyectoGradoDomain crear(UUID proyectoGrado, UUID asesor) {
        var generacion = new GeneracionEntregableProyectoGradoDomain();
        var result = new ValidationResult();

        generacion.setProyectoGrado(proyectoGrado, result);
        generacion.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return generacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.EntregableProyectoGrado.PROYECTO_GRADO,
                EntregablesCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                EntregablesFields.EntregableProyectoGrado.ASESOR,
                EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
