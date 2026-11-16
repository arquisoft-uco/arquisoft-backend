package com.arquisoft.biblioteca.domain.estudianteproyectogrado;

import com.arquisoft.biblioteca.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;

public final class AgregacionEstudiantesProyectoGradoDomain {

    private ProyectoGradoDomain proyecto;
    private List<EstudianteProyectoGradoDomain> vinculos;

    private AgregacionEstudiantesProyectoGradoDomain() {}

    public static AgregacionEstudiantesProyectoGradoDomain crear(
            ProyectoGradoDomain proyecto, List<EstudianteProyectoGradoDomain> vinculos) {
        var agregacion = new AgregacionEstudiantesProyectoGradoDomain();
        var result = new ValidationResult();

        agregacion.setProyecto(proyecto, result);
        agregacion.setVinculos(vinculos, result);

        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setProyecto(ProyectoGradoDomain proyecto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyecto,
                BibliotecaFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyecto = proyecto;
    }

    private void setVinculos(List<EstudianteProyectoGradoDomain> vinculos, ValidationResult result) {
        var lista = UtilColeccion.aplicarPorDefecto(vinculos);
        if (!ValidatorColeccion.noVacia(lista,
                BibliotecaFields.EstudianteProyectoGrado.AUTORES,
                BibliotecaCodes.EstudianteProyectoGrado.AUTORES_REQUERIDOS, result)) {
            return;
        }
        this.vinculos = lista;
    }

    public ProyectoGradoDomain getProyecto() {
        return proyecto;
    }

    public List<EstudianteProyectoGradoDomain> getVinculos() {
        return vinculos;
    }
}
