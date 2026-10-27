package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class AsignacionArtefactosDomain {

    private List<ArtefactoDomain> artefactos;
    private AgregacionEstudiantesArtefactoDomain estudiantes;

    private AsignacionArtefactosDomain() {}

    public static AsignacionArtefactosDomain crear(
            List<ArtefactoDomain> artefactos, AgregacionEstudiantesArtefactoDomain estudiantes) {
        var asignacion = new AsignacionArtefactosDomain();
        var result = new ValidationResult();
        asignacion.setArtefactos(artefactos, result);
        asignacion.setEstudiantes(estudiantes, result);
        result.lanzarSiTieneErrores();
        return asignacion;
    }

    private void setArtefactos(List<ArtefactoDomain> artefactos, ValidationResult result) {
        var lista = UtilColeccion.aplicarPorDefecto(artefactos);
        if (!ValidatorColeccion.noVacia(lista,
                ArtefactosFields.Artefacto.ARTEFACTOS,
                ArtefactosCodes.Artefacto.ARTEFACTOS_REQUERIDOS, result)) {
            return;
        }
        this.artefactos = lista;
    }

    private void setEstudiantes(AgregacionEstudiantesArtefactoDomain estudiantes, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiantes,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.estudiantes = estudiantes;
    }

    public List<UUID> getVersionesRepositorioArtefacto() {
        return artefactos.stream().map(ArtefactoDomain::getVersionRepositorioArtefacto).toList();
    }

    public UUID getProyectoGrado() {
        return artefactos.getFirst().getProyectoGrado();
    }

    public UUID getAsesor() {
        return artefactos.getFirst().getAsesor();
    }

    public List<ArtefactoDomain> getArtefactos() {
        return artefactos;
    }

    public AgregacionEstudiantesArtefactoDomain getEstudiantes() {
        return estudiantes;
    }
}
