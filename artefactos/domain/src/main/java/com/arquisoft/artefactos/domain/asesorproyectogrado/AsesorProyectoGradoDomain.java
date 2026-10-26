package com.arquisoft.artefactos.domain.asesorproyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class AsesorProyectoGradoDomain {

    public static final AsesorProyectoGradoDomain VACIO = AsesorProyectoGradoDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            UtilFecha.VACIO, UtilFecha.VACIO);

    private UUID id;
    private UUID asesor;
    private UUID proyectoGrado;
    private Instant ocurridoEn;
    private Instant finalizadoEn;

    private AsesorProyectoGradoDomain() {}

    public static AsesorProyectoGradoDomain crear(UUID asesor, UUID proyectoGrado, Instant ocurridoEn) {
        var vinculo = new AsesorProyectoGradoDomain();
        var result = new ValidationResult();
        vinculo.setId();
        vinculo.setAsesor(asesor, result);
        vinculo.setProyectoGrado(proyectoGrado, result);
        vinculo.setOcurridoEn(ocurridoEn, result);
        vinculo.setFinalizadoEnInicial();
        result.lanzarSiTieneErrores();
        return vinculo;
    }

    public static AsesorProyectoGradoDomain reconstruir(
            UUID id, UUID asesor, UUID proyectoGrado, Instant ocurridoEn, Instant finalizadoEn) {
        var vinculo = new AsesorProyectoGradoDomain();
        vinculo.id = id;
        vinculo.asesor = asesor;
        vinculo.proyectoGrado = proyectoGrado;
        vinculo.ocurridoEn = ocurridoEn;
        vinculo.finalizadoEn = UtilObjeto.aplicarPorDefecto(finalizadoEn, UtilFecha.VACIO);
        return vinculo;
    }

    public void reactivar(Instant ocurridoEn) {
        var result = new ValidationResult();
        setOcurridoEn(ocurridoEn, result);
        result.lanzarSiTieneErrores();
        this.finalizadoEn = UtilFecha.VACIO;
    }

    public void finalizar(Instant ocurridoEn) {
        var result = new ValidationResult();
        setOcurridoEn(ocurridoEn, result);
        result.lanzarSiTieneErrores();
        this.finalizadoEn = ocurridoEn;
    }

    public boolean estaFinalizado() {
        return !UtilFecha.VACIO.equals(finalizadoEn);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.AsesorProyectoGrado.ASESOR,
                ArtefactosCodes.AsesorProyectoGrado.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                ArtefactosCodes.AsesorProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                ArtefactosFields.AsesorProyectoGrado.OCURRIDO_EN,
                ArtefactosCodes.AsesorProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    private void setFinalizadoEnInicial() {
        this.finalizadoEn = UtilFecha.VACIO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public Instant getFinalizadoEn() {
        return finalizadoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
