package com.arquisoft.entregables.domain.proyectogrado;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CancelacionProyectoGradoDomain {

    private UUID proyectoGrado;
    private EstadoProyecto estadoNuevo;

    private CancelacionProyectoGradoDomain() {}

    public static CancelacionProyectoGradoDomain crear(UUID proyectoGrado) {
        var cancelacion = new CancelacionProyectoGradoDomain();
        var result = new ValidationResult();

        cancelacion.setProyectoGrado(proyectoGrado, result);
        cancelacion.setEstadoNuevoCancelado();

        result.lanzarSiTieneErrores();
        return cancelacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setEstadoNuevoCancelado() {
        this.estadoNuevo = EstadoProyecto.CANCELADO;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public EstadoProyecto getEstadoNuevo() {
        return estadoNuevo;
    }
}
