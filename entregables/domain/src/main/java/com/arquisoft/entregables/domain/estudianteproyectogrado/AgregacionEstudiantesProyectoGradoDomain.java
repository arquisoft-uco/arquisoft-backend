package com.arquisoft.entregables.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class AgregacionEstudiantesProyectoGradoDomain {

    private UUID proyectoGrado;
    private List<EstudianteProyectoGradoDomain> vinculos;

    private AgregacionEstudiantesProyectoGradoDomain() {}

    public static AgregacionEstudiantesProyectoGradoDomain crear(
            UUID proyectoGrado, List<EstudianteProyectoGradoDomain> vinculos) {
        var agregacion = new AgregacionEstudiantesProyectoGradoDomain();
        var result = new ValidationResult();

        agregacion.setProyectoGrado(proyectoGrado, result);
        agregacion.setVinculos(vinculos, result);

        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                EntregablesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setVinculos(List<EstudianteProyectoGradoDomain> vinculos, ValidationResult result) {
        var lista = UtilColeccion.aplicarPorDefecto(vinculos);
        if (!ValidatorColeccion.noVacia(lista,
                EntregablesFields.EstudianteProyectoGrado.ESTUDIANTES,
                EntregablesCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.vinculos = lista;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public List<EstudianteProyectoGradoDomain> getVinculos() {
        return vinculos;
    }
}
