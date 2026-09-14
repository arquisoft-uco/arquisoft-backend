package com.arquisoft.evaluaciones.domain.entregableproyectoacceso;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class EntregableProyectoAccesoDomain {

    public static final EntregableProyectoAccesoDomain VACIO = new EntregableProyectoAccesoDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            0,
            false,
            Instant.EPOCH);

    private static final int VERSION_MINIMA = 1;

    private UUID entregable;
    private UUID proyecto;
    private int versionEntregable;
    private boolean activo;
    private Instant ocurridoEn;

    private EntregableProyectoAccesoDomain() {}

    private EntregableProyectoAccesoDomain(
            UUID entregable, UUID proyecto, int versionEntregable, boolean activo, Instant ocurridoEn) {
        this.entregable = entregable;
        this.proyecto = proyecto;
        this.versionEntregable = versionEntregable;
        this.activo = activo;
        this.ocurridoEn = ocurridoEn;
    }

    public static EntregableProyectoAccesoDomain crear(
            UUID entregable, UUID proyecto, int versionEntregable, Instant ocurridoEn) {
        var acceso = new EntregableProyectoAccesoDomain();
        var result = new ValidationResult();

        acceso.setEntregable(entregable, result);
        acceso.setProyecto(proyecto, result);
        acceso.setVersionEntregable(versionEntregable, result);
        acceso.setActivo();
        acceso.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return acceso;
    }

    public static EntregableProyectoAccesoDomain reconstruir(
            UUID entregable, UUID proyecto, int versionEntregable, boolean activo, Instant ocurridoEn) {
        return new EntregableProyectoAccesoDomain(entregable, proyecto, versionEntregable, activo, ocurridoEn);
    }

    private void setEntregable(UUID entregable, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregable,
                EvaluacionesFields.EntregableProyectoAcceso.ENTREGABLE,
                EvaluacionesCodes.EntregableProyectoAcceso.ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        this.entregable = entregable;
    }

    private void setProyecto(UUID proyecto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyecto,
                EvaluacionesFields.EntregableProyectoAcceso.PROYECTO,
                EvaluacionesCodes.EntregableProyectoAcceso.PROYECTO_REQUERIDO, result)) {
            return;
        }
        this.proyecto = proyecto;
    }

    private void setVersionEntregable(int versionEntregable, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(versionEntregable, VERSION_MINIMA,
                EvaluacionesFields.EntregableProyectoAcceso.VERSION_ENTREGABLE,
                EvaluacionesCodes.EntregableProyectoAcceso.VERSION_ENTREGABLE_INVALIDA, result)) {
            return;
        }
        this.versionEntregable = versionEntregable;
    }

    private void setActivo() {
        this.activo = true;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                EvaluacionesFields.EntregableProyectoAcceso.OCURRIDO_EN,
                EvaluacionesCodes.EntregableProyectoAcceso.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public boolean esMasRecienteQue(EntregableProyectoAccesoDomain existente) {
        return existente.esVacio() || this.ocurridoEn.isAfter(existente.ocurridoEn);
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getEntregable() {
        return entregable;
    }

    public UUID getProyecto() {
        return proyecto;
    }

    public int getVersionEntregable() {
        return versionEntregable;
    }

    public boolean isActivo() {
        return activo;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }
}
