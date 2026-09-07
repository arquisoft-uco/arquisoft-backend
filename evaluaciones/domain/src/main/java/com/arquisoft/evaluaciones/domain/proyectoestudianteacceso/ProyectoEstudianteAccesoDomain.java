package com.arquisoft.evaluaciones.domain.proyectoestudianteacceso;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class ProyectoEstudianteAccesoDomain {

    public static final ProyectoEstudianteAccesoDomain VACIO = new ProyectoEstudianteAccesoDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            false,
            Instant.EPOCH);

    private UUID proyecto;
    private UUID estudiante;
    private boolean activo;
    private Instant ocurridoEn;

    private ProyectoEstudianteAccesoDomain() {}

    private ProyectoEstudianteAccesoDomain(UUID proyecto, UUID estudiante, boolean activo, Instant ocurridoEn) {
        this.proyecto = proyecto;
        this.estudiante = estudiante;
        this.activo = activo;
        this.ocurridoEn = ocurridoEn;
    }

    public static ProyectoEstudianteAccesoDomain crear(
            UUID proyecto, UUID estudiante, boolean activo, Instant ocurridoEn) {
        var acceso = new ProyectoEstudianteAccesoDomain();
        var result = new ValidationResult();

        acceso.setProyecto(proyecto, result);
        acceso.setEstudiante(estudiante, result);
        acceso.setActivo(activo);
        acceso.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return acceso;
    }

    public static ProyectoEstudianteAccesoDomain reconstruir(
            UUID proyecto, UUID estudiante, boolean activo, Instant ocurridoEn) {
        return new ProyectoEstudianteAccesoDomain(proyecto, estudiante, activo, ocurridoEn);
    }

    private void setProyecto(UUID proyecto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyecto,
                EvaluacionesFields.ProyectoEstudianteAcceso.PROYECTO,
                EvaluacionesCodes.ProyectoEstudianteAcceso.PROYECTO_REQUERIDO, result)) {
            return;
        }
        this.proyecto = proyecto;
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                EvaluacionesFields.ProyectoEstudianteAcceso.ESTUDIANTE,
                EvaluacionesCodes.ProyectoEstudianteAcceso.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    private void setActivo(boolean activo) {
        this.activo = activo;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                EvaluacionesFields.ProyectoEstudianteAcceso.OCURRIDO_EN,
                EvaluacionesCodes.ProyectoEstudianteAcceso.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public boolean esMasRecienteQue(ProyectoEstudianteAccesoDomain existente) {
        return existente.esVacio() || this.ocurridoEn.isAfter(existente.ocurridoEn);
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getProyecto() {
        return proyecto;
    }

    public UUID getEstudiante() {
        return estudiante;
    }

    public boolean isActivo() {
        return activo;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }
}
