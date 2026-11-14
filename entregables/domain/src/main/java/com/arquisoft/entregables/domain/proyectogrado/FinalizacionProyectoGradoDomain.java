package com.arquisoft.entregables.domain.proyectogrado;

import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class FinalizacionProyectoGradoDomain {

    private UUID proyectoGrado;
    private EstadoProyecto estadoNuevo;
    private PurgaArtefactosProyectoGradoDomain purgaArtefactos;

    private FinalizacionProyectoGradoDomain() {}

    public static FinalizacionProyectoGradoDomain crear(
            UUID proyectoGrado, PurgaArtefactosProyectoGradoDomain purgaArtefactos) {
        var finalizacion = new FinalizacionProyectoGradoDomain();
        var result = new ValidationResult();

        finalizacion.setProyectoGrado(proyectoGrado, result);
        finalizacion.setEstadoNuevoFinalizado();
        finalizacion.setPurgaArtefactos(purgaArtefactos, result);

        result.lanzarSiTieneErrores();
        return finalizacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setEstadoNuevoFinalizado() {
        this.estadoNuevo = EstadoProyecto.FINALIZADO;
    }

    private void setPurgaArtefactos(PurgaArtefactosProyectoGradoDomain purgaArtefactos, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(purgaArtefactos,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.purgaArtefactos = purgaArtefactos;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public EstadoProyecto getEstadoNuevo() {
        return estadoNuevo;
    }

    public PurgaArtefactosProyectoGradoDomain getPurgaArtefactos() {
        return purgaArtefactos;
    }
}
