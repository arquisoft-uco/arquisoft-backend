package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class DepuracionEstudiantesArtefactoDomain {

    private UUID artefacto;
    private List<UUID> conservar;

    private DepuracionEstudiantesArtefactoDomain() {}

    public static DepuracionEstudiantesArtefactoDomain crear(UUID artefacto, List<UUID> conservar) {
        var depuracion = new DepuracionEstudiantesArtefactoDomain();
        var result = new ValidationResult();
        depuracion.setArtefacto(artefacto, result);
        depuracion.setConservar(conservar, result);
        result.lanzarSiTieneErrores();
        return depuracion;
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setConservar(List<UUID> conservar, ValidationResult result) {
        var lista = UtilColeccion.aplicarPorDefecto(conservar);
        if (!ValidatorColeccion.noVacia(lista,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.conservar = lista;
    }

    public List<UUID> getConservar() {
        return conservar;
    }

    public UUID getArtefacto() {
        return artefacto;
    }
}
