package com.arquisoft.evaluaciones.domain.categoriaitemcuantitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CategoriaItemCuantitativoJuradoDomain {

    private UUID id;
    private String nombre;
    private String descripcion;

    private CategoriaItemCuantitativoJuradoDomain() {}

    private CategoriaItemCuantitativoJuradoDomain(UUID id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static CategoriaItemCuantitativoJuradoDomain crear(String nombre, String descripcion) {
        var categoria = new CategoriaItemCuantitativoJuradoDomain();
        var resultado = new ValidationResult();

        categoria.setId();
        categoria.setNombre(nombre, resultado);
        categoria.setDescripcion(descripcion, resultado);

        resultado.lanzarSiTieneErrores();
        return categoria;
    }

    public static CategoriaItemCuantitativoJuradoDomain reconstruir(
            UUID id, String nombre, String descripcion) {
        return new CategoriaItemCuantitativoJuradoDomain(id, nombre, descripcion);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setNombre(String nombre, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                nombre,
                EvaluacionesFields.CategoriaItemCuantitativoJurado.NOMBRE,
                EvaluacionesCodes.CategoriaItemCuantitativoJurado.NOMBRE_REQUERIDO,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                nombre,
                EvaluacionesLimits.CategoriaItemCuantitativoJurado.NOMBRE_MAX,
                EvaluacionesFields.CategoriaItemCuantitativoJurado.NOMBRE,
                EvaluacionesCodes.CategoriaItemCuantitativoJurado.NOMBRE_DEMASIADO_LARGO,
                resultado)) {
            return;
        }
        this.nombre = UtilTexto.aplicarTrim(nombre);
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.CategoriaItemCuantitativoJurado.DESCRIPCION,
                EvaluacionesCodes.CategoriaItemCuantitativoJurado.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                descripcion,
                EvaluacionesLimits.CategoriaItemCuantitativoJurado.DESCRIPCION_MAX,
                EvaluacionesFields.CategoriaItemCuantitativoJurado.DESCRIPCION,
                EvaluacionesCodes.CategoriaItemCuantitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
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
