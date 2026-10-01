package com.arquisoft.evaluaciones.domain.itemcualitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ItemCualitativoJuradoDomain {

    private UUID id;
    private String nombre;
    private String descripcion;

    private ItemCualitativoJuradoDomain() {}

    private ItemCualitativoJuradoDomain(UUID id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static ItemCualitativoJuradoDomain crear(String nombre, String descripcion) {
        var item = new ItemCualitativoJuradoDomain();
        var resultado = new ValidationResult();

        item.setId();
        item.setNombre(nombre, resultado);
        item.setDescripcion(descripcion, resultado);

        resultado.lanzarSiTieneErrores();
        return item;
    }

    public static ItemCualitativoJuradoDomain reconstruir(
            UUID id, String nombre, String descripcion) {
        return new ItemCualitativoJuradoDomain(id, nombre, descripcion);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setNombre(String nombre, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                nombre,
                EvaluacionesFields.ItemCualitativoJurado.NOMBRE,
                EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_REQUERIDO,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                nombre,
                EvaluacionesLimits.ItemCualitativoJurado.NOMBRE_MAX,
                EvaluacionesFields.ItemCualitativoJurado.NOMBRE,
                EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_DEMASIADO_LARGO,
                resultado)) {
            return;
        }
        this.nombre = UtilTexto.aplicarTrim(nombre);
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoJurado.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                descripcion,
                EvaluacionesLimits.ItemCualitativoJurado.DESCRIPCION_MAX,
                EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
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
