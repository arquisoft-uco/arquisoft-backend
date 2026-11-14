package com.arquisoft.entregables.domain.proyectogrado;

import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CancelacionProyectoGradoDomain {

    private UUID proyectoGrado;
    private EstadoProyecto estadoNuevo;
    private PurgaPaquetesProyectoGradoDomain purgaPaquetes;
    private PurgaArtefactosProyectoGradoDomain purgaArtefactos;

    private CancelacionProyectoGradoDomain() {}

    public static CancelacionProyectoGradoDomain crear(
            UUID proyectoGrado, PurgaPaquetesProyectoGradoDomain purgaPaquetes,
            PurgaArtefactosProyectoGradoDomain purgaArtefactos) {
        var cancelacion = new CancelacionProyectoGradoDomain();
        var result = new ValidationResult();

        cancelacion.setProyectoGrado(proyectoGrado, result);
        cancelacion.setEstadoNuevoCancelado();
        cancelacion.setPurgaPaquetes(purgaPaquetes, result);
        cancelacion.setPurgaArtefactos(purgaArtefactos, result);

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

    private void setPurgaPaquetes(PurgaPaquetesProyectoGradoDomain purgaPaquetes, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(purgaPaquetes,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.purgaPaquetes = purgaPaquetes;
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

    public PurgaPaquetesProyectoGradoDomain getPurgaPaquetes() {
        return purgaPaquetes;
    }

    public PurgaArtefactosProyectoGradoDomain getPurgaArtefactos() {
        return purgaArtefactos;
    }
}
