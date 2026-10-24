package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class AsignacionArtefactosDomain {

    private UUID proyectoGrado;
    private UUID asesor;
    private List<ArtefactoDomain> artefactos;

    private AsignacionArtefactosDomain() {}

    public static AsignacionArtefactosDomain crear(
            UUID proyectoGrado, UUID asesor, List<ArtefactoDomain> artefactos) {
        var asignacion = new AsignacionArtefactosDomain();
        var result = new ValidationResult();
        asignacion.setProyectoGrado(proyectoGrado, result);
        asignacion.setAsesor(asesor, result);
        asignacion.setArtefactos(artefactos, result);
        result.lanzarSiTieneErrores();
        return asignacion;
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

    private void setArtefactos(List<ArtefactoDomain> artefactos, ValidationResult result) {
        var lista = UtilColeccion.aplicarPorDefecto(artefactos);
        if (!ValidatorColeccion.noVacia(lista,
                ArtefactosFields.Artefacto.ARTEFACTOS,
                ArtefactosCodes.Artefacto.ARTEFACTOS_REQUERIDOS, result)) {
            return;
        }
        this.artefactos = lista;
    }

    public List<UUID> getVersionesRepositorioArtefacto() {
        return artefactos.stream().map(ArtefactoDomain::getVersionRepositorioArtefacto).toList();
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public List<ArtefactoDomain> getArtefactos() {
        return artefactos;
    }
}
