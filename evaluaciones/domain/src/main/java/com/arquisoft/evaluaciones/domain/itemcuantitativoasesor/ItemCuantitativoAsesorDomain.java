package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor;

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

public final class ItemCuantitativoAsesorDomain {

    private UUID id;
    private String nombre;
    private String descripcion;
    private UUID categoria;
    private Integer valor;

    private ItemCuantitativoAsesorDomain() {}

    private ItemCuantitativoAsesorDomain(
            UUID id, String nombre, String descripcion, UUID categoria, Integer valor) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.valor = valor;
    }

    public static ItemCuantitativoAsesorDomain crear(
            String nombre, String descripcion, UUID categoria, Integer valor) {
        var item = new ItemCuantitativoAsesorDomain();
        var resultado = new ValidationResult();

        item.setId();
        item.setNombre(nombre, resultado);
        item.setDescripcion(descripcion, resultado);
        item.setCategoria(categoria, resultado);
        item.setValor(valor, resultado);

        resultado.lanzarSiTieneErrores();
        return item;
    }

    public static ItemCuantitativoAsesorDomain reconstruir(
            UUID id, String nombre, String descripcion, UUID categoria, Integer valor) {
        return new ItemCuantitativoAsesorDomain(id, nombre, descripcion, categoria, valor);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setNombre(String nombre, ValidationResult resultado) {
        var recortado = UtilTexto.aplicarTrim(nombre);
        if (!ValidatorTexto.noEnBlanco(
                recortado,
                EvaluacionesFields.ItemCuantitativoAsesor.NOMBRE,
                EvaluacionesCodes.ItemCuantitativoAsesor.NOMBRE_REQUERIDO,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                recortado,
                EvaluacionesLimits.ItemCuantitativoAsesor.NOMBRE_MAX,
                EvaluacionesFields.ItemCuantitativoAsesor.NOMBRE,
                EvaluacionesCodes.ItemCuantitativoAsesor.NOMBRE_DEMASIADO_LARGO,
                resultado)) {
            return;
        }
        this.nombre = recortado;
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        var recortada = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(
                recortada,
                EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                recortada,
                EvaluacionesLimits.ItemCuantitativoAsesor.DESCRIPCION_MAX,
                EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_DEMASIADO_LARGA,
                resultado)) {
            return;
        }
        this.descripcion = recortada;
    }

    private void setCategoria(UUID categoria, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                categoria,
                EvaluacionesFields.ItemCuantitativoAsesor.CATEGORIA,
                EvaluacionesCodes.ItemCuantitativoAsesor.CATEGORIA_REQUERIDA,
                resultado)) {
            return;
        }
        this.categoria = categoria;
    }

    private void setValor(Integer valor, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                valor,
                EvaluacionesFields.ItemCuantitativoAsesor.VALOR,
                EvaluacionesCodes.ItemCuantitativoAsesor.VALOR_REQUERIDO,
                resultado)) {
            return;
        }
        if (!ValidatorNumero.valorEntre(
                valor,
                EvaluacionesLimits.ItemCuantitativoAsesor.VALOR_MIN,
                EvaluacionesLimits.ItemCuantitativoAsesor.VALOR_MAX,
                EvaluacionesFields.ItemCuantitativoAsesor.VALOR,
                EvaluacionesCodes.ItemCuantitativoAsesor.VALOR_FUERA_DE_RANGO,
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
