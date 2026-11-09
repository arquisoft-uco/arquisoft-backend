package com.arquisoft.entregables.domain.artefactoentregable;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class AgregacionArtefactosEntregableDomain {

    private UUID entregableProyectoGrado;
    private List<ArtefactoEntregableDomain> artefactos;

    private AgregacionArtefactosEntregableDomain() {}

    public static AgregacionArtefactosEntregableDomain crear(
            UUID entregableProyectoGrado, List<ArtefactoEntregableDomain> artefactos) {
        var agregacion = new AgregacionArtefactosEntregableDomain();
        var result = new ValidationResult();
        agregacion.setEntregableProyectoGrado(entregableProyectoGrado, result);
        agregacion.setArtefactos(artefactos);
        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setEntregableProyectoGrado(UUID entregableProyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregableProyectoGrado,
                EntregablesFields.ArtefactoEntregable.ENTREGABLE_PROYECTO_GRADO,
                EntregablesCodes.ArtefactoEntregable.ENTREGABLE_PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.entregableProyectoGrado = entregableProyectoGrado;
    }

    private void setArtefactos(List<ArtefactoEntregableDomain> artefactos) {
        this.artefactos = UtilColeccion.aplicarPorDefecto(artefactos);
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public List<ArtefactoEntregableDomain> getArtefactos() {
        return artefactos;
    }
}
