package com.arquisoft.proyectos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class EstudianteProyectoGradoDomain {

    private UUID id;
    private UUID estudiante;
    private UUID proyectoGrado;

    private EstudianteProyectoGradoDomain() {}

    private static EstudianteProyectoGradoDomain crear(UUID proyectoGrado, UUID estudiante) {
        var vinculo = new EstudianteProyectoGradoDomain();
        var result = new ValidationResult();

        vinculo.setId();
        vinculo.setProyectoGrado(proyectoGrado, result);
        vinculo.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return vinculo;
    }

    public static List<EstudianteProyectoGradoDomain> crear(UUID proyectoGrado, List<UUID> estudiantes) {
        var result = new ValidationResult();

        ValidatorColeccion.noVacia(estudiantes,
                ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES,
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS, result);

        result.lanzarSiTieneErrores();

        return estudiantes.stream()
                .map(estudiante -> crear(proyectoGrado, estudiante))
                .toList();
    }

    public static EstudianteProyectoGradoDomain reconstruir(UUID id, UUID estudiante, UUID proyectoGrado) {
        var vinculo = new EstudianteProyectoGradoDomain();
        vinculo.id = id;
        vinculo.estudiante = estudiante;
        vinculo.proyectoGrado = proyectoGrado;
        return vinculo;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
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

    public UUID getId() {
        return id;
    }

    public UUID getEstudiante() {
        return estudiante;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }
}
