package com.arquisoft.evaluaciones.domain.itemcuantitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ItemCuantitativoJuradoDomain {

    private UUID id;
    private String nombre;
    private String descripcion;
    private UUID categoria;
    private Integer valor;

    private ItemCuantitativoJuradoDomain() {}

    private ItemCuantitativoJuradoDomain(
            UUID id, String nombre, String descripcion, UUID categoria, Integer valor) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.valor = valor;
    }

    public static ItemCuantitativoJuradoDomain crear(
            String nombre, String descripcion, UUID categoria, Integer valor) {
        var item = new ItemCuantitativoJuradoDomain();
        var resultado = new ValidationResult();

        item.setId();
        item.setNombre(nombre, resultado);
        item.setDescripcion(descripcion, resultado);
        item.setCategoria(categoria, resultado);
        item.setValor(valor, resultado);

        resultado.lanzarSiTieneErrores();
        return item;
    }

    public static ItemCuantitativoJuradoDomain reconstruir(
            UUID id, String nombre, String descripcion, UUID categoria, Integer valor) {
        return new ItemCuantitativoJuradoDomain(id, nombre, descripcion, categoria, valor);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setNombre(String nombre, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                nombre,
                EvaluacionesFields.ItemCuantitativoJurado.NOMBRE,
                EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_REQUERIDO,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                nombre,
                EvaluacionesLimits.ItemCuantitativoJurado.NOMBRE_MAX,
                EvaluacionesFields.ItemCuantitativoJurado.NOMBRE,
                EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_DEMASIADO_LARGO,
                resultado)) {
            return;
        }
        this.nombre = UtilTexto.aplicarTrim(nombre);
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                descripcion,
                EvaluacionesLimits.ItemCuantitativoJurado.DESCRIPCION_MAX,
                EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
                resultado)) {
            return;
        }
        this.descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    private void setCategoria(UUID categoria, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                categoria,
                EvaluacionesFields.ItemCuantitativoJurado.CATEGORIA,
                EvaluacionesCodes.ItemCuantitativoJurado.CATEGORIA_REQUERIDA,
                resultado)) {
            return;
        }
        this.categoria = categoria;
    }

    private void setValor(Integer valor, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                valor,
                EvaluacionesFields.ItemCuantitativoJurado.VALOR,
                EvaluacionesCodes.ItemCuantitativoJurado.VALOR_REQUERIDO,
                resultado)) {
            return;
        }
        if (!ValidatorNumero.valorEntre(
                valor,
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MIN,
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MAX,
                EvaluacionesFields.ItemCuantitativoJurado.VALOR,
                EvaluacionesCodes.ItemCuantitativoJurado.VALOR_FUERA_DE_RANGO,
                resultado)) {
            return;
        }
        this.valor = valor;
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

    public UUID getCategoria() {
        return categoria;
    }

    public Integer getValor() {
        return valor;
    }
}
