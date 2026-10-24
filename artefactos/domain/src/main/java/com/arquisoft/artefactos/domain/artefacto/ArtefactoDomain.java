package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class ArtefactoDomain {

    private UUID id;
    private UUID proyectoGrado;
    private UUID versionRepositorioArtefacto;
    private UUID asesor;

    private ArtefactoDomain() {}

    public static ArtefactoDomain crear(UUID proyectoGrado, UUID versionRepositorioArtefacto, UUID asesor) {
        var artefacto = new ArtefactoDomain();
        var result = new ValidationResult();
        artefacto.setId();
        artefacto.setProyectoGrado(proyectoGrado, result);
        artefacto.setVersionRepositorioArtefacto(versionRepositorioArtefacto, result);
        artefacto.setAsesor(asesor, result);
        result.lanzarSiTieneErrores();
        return artefacto;
    }

    public static ArtefactoDomain reconstruir(
            UUID id, UUID proyectoGrado, UUID versionRepositorioArtefacto, UUID asesor) {
        var artefacto = new ArtefactoDomain();
        artefacto.id = id;
        artefacto.proyectoGrado = proyectoGrado;
        artefacto.versionRepositorioArtefacto = versionRepositorioArtefacto;
        artefacto.asesor = asesor;
        return artefacto;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.Artefacto.PROYECTO_GRADO,
                ArtefactosCodes.Artefacto.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setVersionRepositorioArtefacto(UUID versionRepositorioArtefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(versionRepositorioArtefacto,
                ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO,
                ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA, result)) {
            return;
        }
        this.versionRepositorioArtefacto = versionRepositorioArtefacto;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getVersionRepositorioArtefacto() {
        return versionRepositorioArtefacto;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
