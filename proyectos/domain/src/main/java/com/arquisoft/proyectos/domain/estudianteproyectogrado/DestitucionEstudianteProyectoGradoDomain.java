package com.arquisoft.proyectos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class DestitucionEstudianteProyectoGradoDomain {

    private UUID proyectoGrado;
    private UUID estudiante;
    private UUID coordinador;

    private DestitucionEstudianteProyectoGradoDomain() {}

    public static DestitucionEstudianteProyectoGradoDomain crear(UUID proyectoGrado, UUID estudiante,
                                                                 UUID coordinador) {
        var destitucion = new DestitucionEstudianteProyectoGradoDomain();
        var result = new ValidationResult();

        destitucion.setProyectoGrado(proyectoGrado, result);
        destitucion.setEstudiante(estudiante, result);
        destitucion.setCoordinador(coordinador, result);

        result.lanzarSiTieneErrores();
        return destitucion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                ProyectosFields.EstudianteProyectoGrado.ESTUDIANTE,
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.EstudianteProyectoGrado.COORDINADOR,
                ProyectosCodes.EstudianteProyectoGrado.COORDINADOR_ID_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getEstudiante() {
        return estudiante;
    }

    public UUID getCoordinador() {
        return coordinador;
    }
}
