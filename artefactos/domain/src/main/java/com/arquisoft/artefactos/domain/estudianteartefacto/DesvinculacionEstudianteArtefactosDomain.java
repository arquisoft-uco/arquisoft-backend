package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class DesvinculacionEstudianteArtefactosDomain {

    private UUID proyectoGrado;
    private UUID estudiante;

    private DesvinculacionEstudianteArtefactosDomain() {}

    public static DesvinculacionEstudianteArtefactosDomain crear(UUID proyectoGrado, UUID estudiante) {
        var desvinculacion = new DesvinculacionEstudianteArtefactosDomain();
        var result = new ValidationResult();
        desvinculacion.setProyectoGrado(proyectoGrado, result);
        desvinculacion.setEstudiante(estudiante, result);
        result.lanzarSiTieneErrores();
        return desvinculacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.EstudianteArtefacto.PROYECTO_GRADO,
                ArtefactosCodes.EstudianteArtefacto.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTE,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
