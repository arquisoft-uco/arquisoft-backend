package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionEstudianteArtefactoDomain {

    private UUID artefacto;
    private UUID asesor;
    private UUID estudiante;

    private RemocionEstudianteArtefactoDomain() {}

    public static RemocionEstudianteArtefactoDomain crear(UUID artefacto, UUID asesor, UUID estudiante) {
        var remocion = new RemocionEstudianteArtefactoDomain();
        var result = new ValidationResult();

        remocion.setArtefacto(artefacto, result);
        remocion.setAsesor(asesor, result);
        remocion.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.EstudianteArtefacto.ASESOR,
                ArtefactosCodes.EstudianteArtefacto.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTE,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
