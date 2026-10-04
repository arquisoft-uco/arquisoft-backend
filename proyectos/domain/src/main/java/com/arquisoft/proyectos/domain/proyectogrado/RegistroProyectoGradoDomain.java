package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

public final class RegistroProyectoGradoDomain {

    private ProyectoGradoDomain proyecto;
    private AgregacionEstudiantesProyectoGradoDomain estudiantes;

    private RegistroProyectoGradoDomain() {}

    public static RegistroProyectoGradoDomain crear(ProyectoGradoDomain proyecto,
                                                     AgregacionEstudiantesProyectoGradoDomain estudiantes) {
        var registro = new RegistroProyectoGradoDomain();
        var result = new ValidationResult();

        registro.setProyecto(proyecto, result);
        registro.setEstudiantes(estudiantes, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setProyecto(ProyectoGradoDomain proyecto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyecto,
                ProyectosFields.ProyectoGrado.PROYECTO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyecto = proyecto;
    }

    private void setEstudiantes(AgregacionEstudiantesProyectoGradoDomain estudiantes, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiantes,
                ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES,
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.estudiantes = estudiantes;
    }

    public ProyectoGradoDomain getProyecto() {
        return proyecto;
    }

    public AgregacionEstudiantesProyectoGradoDomain getEstudiantes() {
        return estudiantes;
    }
}
