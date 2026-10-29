package com.arquisoft.artefactos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class EstudianteProyectoGradoDomain {

    public static final EstudianteProyectoGradoDomain VACIO = EstudianteProyectoGradoDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            UtilFecha.VACIO, UtilFecha.VACIO);

    private UUID id;
    private UUID estudiante;
    private UUID proyectoGrado;
    private Instant ocurridoEn;
    private Instant destituidoEn;

    private EstudianteProyectoGradoDomain() {}

    public static EstudianteProyectoGradoDomain crear(UUID estudiante, UUID proyectoGrado, Instant ocurridoEn) {
        var vinculo = new EstudianteProyectoGradoDomain();
        var result = new ValidationResult();
        vinculo.setId();
        vinculo.setEstudiante(estudiante, result);
        vinculo.setProyectoGrado(proyectoGrado, result);
        vinculo.setOcurridoEn(ocurridoEn, result);
        vinculo.setDestituidoEnInicial();
        result.lanzarSiTieneErrores();
        return vinculo;
    }

    public static EstudianteProyectoGradoDomain reconstruir(
            UUID id, UUID estudiante, UUID proyectoGrado, Instant ocurridoEn, Instant destituidoEn) {
        var vinculo = new EstudianteProyectoGradoDomain();
        vinculo.id = id;
        vinculo.estudiante = estudiante;
        vinculo.proyectoGrado = proyectoGrado;
        vinculo.ocurridoEn = ocurridoEn;
        vinculo.destituidoEn = UtilObjeto.aplicarPorDefecto(destituidoEn, UtilFecha.VACIO);
        return vinculo;
    }

    public void reactivar(Instant ocurridoEn) {
        var result = new ValidationResult();
        setOcurridoEn(ocurridoEn, result);
        result.lanzarSiTieneErrores();
        this.destituidoEn = UtilFecha.VACIO;
    }

    public void destituir(Instant ocurridoEn) {
        var result = new ValidationResult();
        setOcurridoEn(ocurridoEn, result);
        result.lanzarSiTieneErrores();
        this.destituidoEn = ocurridoEn;
    }

    public boolean estaDestituido() {
        return !UtilFecha.VACIO.equals(destituidoEn);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.EstudianteProyectoGrado.ESTUDIANTE,
                ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                ArtefactosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                ArtefactosFields.EstudianteProyectoGrado.OCURRIDO_EN,
                ArtefactosCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    private void setDestituidoEnInicial() {
        this.destituidoEn = UtilFecha.VACIO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEstudiante() {
        return estudiante;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public Instant getDestituidoEn() {
        return destituidoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
