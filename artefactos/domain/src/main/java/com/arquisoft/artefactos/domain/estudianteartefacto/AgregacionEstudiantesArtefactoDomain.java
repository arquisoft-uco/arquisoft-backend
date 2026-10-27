package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class AgregacionEstudiantesArtefactoDomain {

    public static final AgregacionEstudiantesArtefactoDomain VACIO = vacio();

    private UUID asesor;
    private List<EstudianteArtefactoDomain> relaciones;

    private AgregacionEstudiantesArtefactoDomain() {}

    public static AgregacionEstudiantesArtefactoDomain crear(
            UUID asesor, List<EstudianteArtefactoDomain> relaciones) {
        var agregacion = new AgregacionEstudiantesArtefactoDomain();
        var result = new ValidationResult();

        agregacion.setAsesor(asesor, result);
        agregacion.setRelaciones(relaciones, result);

        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private static AgregacionEstudiantesArtefactoDomain vacio() {
        var agregacion = new AgregacionEstudiantesArtefactoDomain();
        agregacion.asesor = UtilUUID.obtenerUUIDPorDefecto();
        agregacion.relaciones = List.of();
        return agregacion;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.EstudianteArtefacto.ASESOR,
                ArtefactosCodes.EstudianteArtefacto.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setRelaciones(List<EstudianteArtefactoDomain> relaciones, ValidationResult result) {
        var lista = UtilColeccion.aplicarPorDefecto(relaciones);
        if (!ValidatorColeccion.noVacia(lista,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.relaciones = lista;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public List<EstudianteArtefactoDomain> getRelaciones() {
        return relaciones;
    }

    public List<UUID> getArtefactos() {
        return relaciones.stream().map(EstudianteArtefactoDomain::getArtefacto).distinct().toList();
    }

    public List<UUID> getEstudiantes() {
        return relaciones.stream().map(EstudianteArtefactoDomain::getEstudiante).distinct().toList();
    }

    public List<ParEstudianteArtefacto> getPares() {
        return relaciones.stream()
                .map(relacion -> new ParEstudianteArtefacto(relacion.getArtefacto(), relacion.getEstudiante()))
                .toList();
    }

    public int getCantidad() {
        return relaciones.size();
    }
}
