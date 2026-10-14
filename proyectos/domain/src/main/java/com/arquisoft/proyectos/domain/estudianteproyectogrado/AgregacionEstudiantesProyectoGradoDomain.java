package com.arquisoft.proyectos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class AgregacionEstudiantesProyectoGradoDomain {

    private List<EstudianteProyectoGradoDomain> relaciones;
    private UUID coordinador;

    private AgregacionEstudiantesProyectoGradoDomain() {}

    public static AgregacionEstudiantesProyectoGradoDomain crear(List<EstudianteProyectoGradoDomain> relaciones,
                                                                 UUID coordinador) {
        var agregacion = new AgregacionEstudiantesProyectoGradoDomain();
        var result = new ValidationResult();

        agregacion.setRelaciones(relaciones, result);
        agregacion.setCoordinador(coordinador, result);

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

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.EstudianteProyectoGrado.COORDINADOR,
                ProyectosCodes.EstudianteProyectoGrado.COORDINADOR_ID_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    public List<EstudianteProyectoGradoDomain> getRelaciones() {
        return relaciones;
    }

    public UUID getCoordinador() {
        return coordinador;
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
