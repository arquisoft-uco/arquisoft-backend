package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class EstudianteArtefactoDomain {

    private UUID id;
    private UUID artefacto;
    private UUID estudiante;

    private EstudianteArtefactoDomain() {}

    private static EstudianteArtefactoDomain crear(UUID artefacto, UUID estudiante) {
        var relacion = new EstudianteArtefactoDomain();
        var result = new ValidationResult();
        relacion.setId();
        relacion.setArtefacto(artefacto, result);
        relacion.setEstudiante(estudiante, result);
        result.lanzarSiTieneErrores();
        return relacion;
    }

    public static List<EstudianteArtefactoDomain> crear(List<UUID> artefactos, List<UUID> estudiantes) {
        var result = new ValidationResult();
        var artefactosLista = UtilColeccion.aplicarPorDefecto(artefactos);
        var estudiantesLista = UtilColeccion.aplicarPorDefecto(estudiantes);

        if (ValidatorColeccion.noVacia(artefactosLista,
                ArtefactosFields.EstudianteArtefacto.ARTEFACTOS,
                ArtefactosCodes.Artefacto.ARTEFACTOS_REQUERIDOS, result)) {
            ValidatorColeccion.sinDuplicados(artefactosLista,
                    ArtefactosFields.EstudianteArtefacto.ARTEFACTOS,
                    ArtefactosCodes.EstudianteArtefacto.ARTEFACTOS_DUPLICADOS, result);
        }
        if (ValidatorColeccion.noVacia(estudiantesLista,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS, result)) {
            ValidatorColeccion.sinDuplicados(estudiantesLista,
                    ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                    ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_DUPLICADOS, result);
        }

        result.lanzarSiTieneErrores();

        return artefactosLista.stream()
                .flatMap(artefacto -> estudiantesLista.stream().map(estudiante -> crear(artefacto, estudiante)))
                .toList();
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTE,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_ID_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
