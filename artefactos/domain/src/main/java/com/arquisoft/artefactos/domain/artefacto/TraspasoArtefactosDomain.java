package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class TraspasoArtefactosDomain {

    private UUID proyectoGrado;
    private UUID asesor;

    private TraspasoArtefactosDomain() {}

    public static TraspasoArtefactosDomain crear(UUID proyectoGrado, UUID asesor) {
        var traspaso = new TraspasoArtefactosDomain();
        var result = new ValidationResult();
        traspaso.setProyectoGrado(proyectoGrado, result);
        traspaso.setAsesor(asesor, result);
        result.lanzarSiTieneErrores();
        return traspaso;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.Artefacto.PROYECTO_GRADO,
                ArtefactosCodes.Artefacto.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result)) {
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
