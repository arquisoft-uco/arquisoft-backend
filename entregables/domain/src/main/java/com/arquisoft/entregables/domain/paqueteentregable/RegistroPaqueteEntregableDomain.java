package com.arquisoft.entregables.domain.paqueteentregable;

import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

public final class RegistroPaqueteEntregableDomain {

    private PaqueteEntregableDomain paquete;
    private AgregacionArtefactosEntregableDomain artefactos;

    private RegistroPaqueteEntregableDomain() {}

    public static RegistroPaqueteEntregableDomain crear(
            PaqueteEntregableDomain paquete, AgregacionArtefactosEntregableDomain artefactos) {
        var registro = new RegistroPaqueteEntregableDomain();
        var result = new ValidationResult();
        registro.setPaquete(paquete, result);
        registro.setArtefactos(artefactos, result);
        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setPaquete(PaqueteEntregableDomain paquete, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(paquete,
                EntregablesFields.ArtefactoEntregable.PAQUETE,
                EntregablesCodes.ArtefactoEntregable.PAQUETE_REQUERIDO, result)) {
            return;
        }
        this.paquete = paquete;
    }

    private void setArtefactos(AgregacionArtefactosEntregableDomain artefactos, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefactos,
                EntregablesFields.ArtefactoEntregable.ARTEFACTOS,
                EntregablesCodes.ArtefactoEntregable.ARTEFACTOS_REQUERIDOS, result)) {
            return;
        }
        this.artefactos = artefactos;
    }

    public PaqueteEntregableDomain getPaquete() {
        return paquete;
    }

    public AgregacionArtefactosEntregableDomain getArtefactos() {
        return artefactos;
    }
}
