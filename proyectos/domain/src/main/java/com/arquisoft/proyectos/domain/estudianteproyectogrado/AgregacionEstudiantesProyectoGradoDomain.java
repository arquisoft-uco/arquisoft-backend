package com.arquisoft.proyectos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;

import java.util.List;
import java.util.UUID;

public final class AgregacionEstudiantesProyectoGradoDomain {

    private List<EstudianteProyectoGradoDomain> relaciones;

    private AgregacionEstudiantesProyectoGradoDomain() {}

    public static AgregacionEstudiantesProyectoGradoDomain crear(List<EstudianteProyectoGradoDomain> relaciones) {
        var agregacion = new AgregacionEstudiantesProyectoGradoDomain();
        var result = new ValidationResult();

        agregacion.setRelaciones(relaciones, result);

        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setRelaciones(List<EstudianteProyectoGradoDomain> relaciones, ValidationResult result) {
        var lista = UtilColeccion.aplicarPorDefecto(relaciones);
        if (!ValidatorColeccion.noVacia(lista,
                ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES,
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.relaciones = lista;
    }

    public List<EstudianteProyectoGradoDomain> getRelaciones() {
        return relaciones;
    }

    public UUID getProyectoGrado() {
        return relaciones.getFirst().getProyectoGrado();
    }

    public List<UUID> getEstudiantes() {
        return relaciones.stream().map(EstudianteProyectoGradoDomain::getEstudiante).toList();
    }

    public int getCantidad() {
        return relaciones.size();
    }
}
