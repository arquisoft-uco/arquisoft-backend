package com.arquisoft.evaluaciones.domain.criterioitemcualitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CriterioItemCualitativoJuradoDomain {

    private UUID id;
    private String nombre;
    private String descripcion;

    private CriterioItemCualitativoJuradoDomain() {}

    private CriterioItemCualitativoJuradoDomain(UUID id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static CriterioItemCualitativoJuradoDomain crear(String nombre, String descripcion) {
        var criterio = new CriterioItemCualitativoJuradoDomain();
        var resultado = new ValidationResult();

        criterio.setId();
        criterio.setNombre(nombre, resultado);
        criterio.setDescripcion(descripcion, resultado);

        resultado.lanzarSiTieneErrores();
        return criterio;
    }

    public static CriterioItemCualitativoJuradoDomain reconstruir(
            UUID id, String nombre, String descripcion) {
        return new CriterioItemCualitativoJuradoDomain(id, nombre, descripcion);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setNombre(String nombre, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                nombre,
                EvaluacionesFields.CriterioItemCualitativoJurado.NOMBRE,
                EvaluacionesCodes.CriterioItemCualitativoJurado.NOMBRE_REQUERIDO,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                nombre,
                EvaluacionesLimits.CriterioItemCualitativoJurado.NOMBRE_MAX,
                EvaluacionesFields.CriterioItemCualitativoJurado.NOMBRE,
                EvaluacionesCodes.CriterioItemCualitativoJurado.NOMBRE_DEMASIADO_LARGO,
                resultado)) {
            return;
        }
        this.nombre = UtilTexto.aplicarTrim(nombre);
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.CriterioItemCualitativoJurado.DESCRIPCION,
                EvaluacionesCodes.CriterioItemCualitativoJurado.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                descripcion,
                EvaluacionesLimits.CriterioItemCualitativoJurado.DESCRIPCION_MAX,
                EvaluacionesFields.CriterioItemCualitativoJurado.DESCRIPCION,
                EvaluacionesCodes.CriterioItemCualitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
                resultado)) {
            return;
        }
        this.descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
