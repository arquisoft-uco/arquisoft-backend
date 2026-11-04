package com.arquisoft.artefactos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;

import java.util.List;
import java.util.UUID;

public final class AgregacionEstudiantesProyectoGradoDomain {

    private List<EstudianteProyectoGradoDomain> vinculos;

    private AgregacionEstudiantesProyectoGradoDomain() {}

    public static AgregacionEstudiantesProyectoGradoDomain crear(List<EstudianteProyectoGradoDomain> vinculos) {
        var agregacion = new AgregacionEstudiantesProyectoGradoDomain();
        var result = new ValidationResult();
        agregacion.setVinculos(vinculos, result);
        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setVinculos(List<EstudianteProyectoGradoDomain> vinculos, ValidationResult result) {
        var lista = UtilColeccion.aplicarPorDefecto(vinculos);
        if (!ValidatorColeccion.noVacia(lista,
                ArtefactosFields.EstudianteProyectoGrado.VINCULOS,
                ArtefactosCodes.EstudianteProyectoGrado.VINCULOS_REQUERIDOS, result)) {
            return;
        }
        var estudiantes = lista.stream().map(EstudianteProyectoGradoDomain::getEstudiante).toList();
        if (!ValidatorColeccion.sinDuplicados(estudiantes,
                ArtefactosFields.EstudianteProyectoGrado.ESTUDIANTES,
                ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTES_DUPLICADOS, result)) {
            return;
        }
        this.vinculos = lista;
    }

    public List<EstudianteProyectoGradoDomain> getVinculos() {
        return vinculos;
    }

    public UUID getProyectoGrado() {
        return vinculos.get(0).getProyectoGrado();
    }

    public List<UUID> getEstudiantes() {
        return vinculos.stream().map(EstudianteProyectoGradoDomain::getEstudiante).toList();
    }
}
