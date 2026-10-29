package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

public final class AsignacionArtefactoDomain {

    private ArtefactoDomain artefacto;
    private AgregacionEstudiantesArtefactoDomain estudiantes;

    private AsignacionArtefactoDomain() {}

    public static AsignacionArtefactoDomain crear(
            ArtefactoDomain artefacto, AgregacionEstudiantesArtefactoDomain estudiantes) {
        var asignacion = new AsignacionArtefactoDomain();
        var result = new ValidationResult();
        asignacion.setArtefacto(artefacto, result);
        asignacion.setEstudiantes(estudiantes, result);
        result.lanzarSiTieneErrores();
        return asignacion;
    }

    private void setArtefacto(ArtefactoDomain artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.Artefacto.ID,
                ArtefactosCodes.Artefacto.ID_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setEstudiantes(AgregacionEstudiantesArtefactoDomain estudiantes, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiantes,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.estudiantes = estudiantes;
    }

    public ArtefactoDomain getArtefacto() {
        return artefacto;
    }

    public AgregacionEstudiantesArtefactoDomain getEstudiantes() {
        return estudiantes;
    }
}
