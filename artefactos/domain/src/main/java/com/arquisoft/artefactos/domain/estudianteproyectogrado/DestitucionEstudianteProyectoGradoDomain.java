package com.arquisoft.artefactos.domain.estudianteproyectogrado;

import com.arquisoft.artefactos.domain.estudianteartefacto.DesvinculacionEstudianteArtefactosDomain;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

public final class DestitucionEstudianteProyectoGradoDomain {

    private EstudianteProyectoGradoDomain vinculo;
    private DesvinculacionEstudianteArtefactosDomain desvinculacion;

    private DestitucionEstudianteProyectoGradoDomain() {}

    public static DestitucionEstudianteProyectoGradoDomain crear(
            EstudianteProyectoGradoDomain vinculo, DesvinculacionEstudianteArtefactosDomain desvinculacion) {
        var destitucion = new DestitucionEstudianteProyectoGradoDomain();
        var result = new ValidationResult();
        destitucion.setVinculo(vinculo, result);
        destitucion.setDesvinculacion(desvinculacion, result);
        result.lanzarSiTieneErrores();
        return destitucion;
    }

    private void setVinculo(EstudianteProyectoGradoDomain vinculo, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(vinculo,
                ArtefactosFields.EstudianteProyectoGrado.VINCULO,
                ArtefactosCodes.EstudianteProyectoGrado.VINCULO_REQUERIDO, result)) {
            return;
        }
        this.vinculo = vinculo;
    }

    private void setDesvinculacion(DesvinculacionEstudianteArtefactosDomain desvinculacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(desvinculacion,
                ArtefactosFields.EstudianteProyectoGrado.DESVINCULACION,
                ArtefactosCodes.EstudianteProyectoGrado.DESVINCULACION_REQUERIDA, result)) {
            return;
        }
        this.desvinculacion = desvinculacion;
    }

    public EstudianteProyectoGradoDomain getVinculo() {
        return vinculo;
    }

    public DesvinculacionEstudianteArtefactosDomain getDesvinculacion() {
        return desvinculacion;
    }
}
