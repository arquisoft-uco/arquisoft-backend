package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class ReactivacionArtefactoDomain {

    private UUID artefacto;
    private UUID proyectoGrado;
    private UUID asesor;
    private List<UUID> estudiantes;

    private ReactivacionArtefactoDomain() {}

    public static ReactivacionArtefactoDomain crear(
            UUID artefacto, UUID proyectoGrado, UUID asesor, List<UUID> estudiantes) {
        var reactivacion = new ReactivacionArtefactoDomain();
        var result = new ValidationResult();
        reactivacion.setArtefacto(artefacto, result);
        reactivacion.setProyectoGrado(proyectoGrado, result);
        reactivacion.setAsesor(asesor, result);
        reactivacion.setEstudiantes(estudiantes, result);
        result.lanzarSiTieneErrores();
        return reactivacion;
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.Artefacto.ID,
                ArtefactosCodes.Artefacto.ID_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.Artefacto.PROYECTO_GRADO,
                ArtefactosCodes.Artefacto.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setEstudiantes(List<UUID> estudiantes, ValidationResult result) {
        var lista = UtilColeccion.aplicarPorDefecto(estudiantes);
        if (!ValidatorColeccion.noVacia(lista,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.estudiantes = lista;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public List<UUID> getEstudiantes() {
        return estudiantes;
    }
}
