package com.arquisoft.evaluaciones.domain.itemcualitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ItemCualitativoAsesorDomain {

    private UUID id;
    private String nombre;
    private String descripcion;
    private int peso;

    private ItemCualitativoAsesorDomain() {}

    public static ItemCualitativoAsesorDomain crear(String nombre, String descripcion, Integer peso) {
        var item = new ItemCualitativoAsesorDomain();
        var resultado = new ValidationResult();

        item.setId();
        item.setNombre(nombre, resultado);
        item.setDescripcion(descripcion, resultado);
        item.setPeso(peso, resultado);

        resultado.lanzarSiTieneErrores();
        return item;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setNombre(String nombre, ValidationResult resultado) {
        var recortado = UtilTexto.aplicarTrim(nombre);
        if (!ValidatorTexto.noEnBlanco(
                recortado,
                EvaluacionesFields.ItemCualitativoAsesor.NOMBRE,
                EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_REQUERIDO,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                recortado,
                EvaluacionesLimits.ItemCualitativoAsesor.NOMBRE_MAX,
                EvaluacionesFields.ItemCualitativoAsesor.NOMBRE,
                EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_DEMASIADO_LARGO,
                resultado)) {
            return;
        }
        this.nombre = recortado;
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        var recortada = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(
                recortada,
                EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                recortada,
                EvaluacionesLimits.ItemCualitativoAsesor.DESCRIPCION_MAX,
                EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_DEMASIADO_LARGA,
                resultado)) {
            return;
        }
        this.descripcion = recortada;
    }

    private void setPeso(Integer peso, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                peso,
                EvaluacionesFields.ItemCualitativoAsesor.PESO,
                EvaluacionesCodes.ItemCualitativoAsesor.PESO_REQUERIDO,
                resultado)) {
            return;
        }
        if (!ValidatorNumero.valorEntre(
                peso,
                EvaluacionesLimits.ItemCualitativoAsesor.PESO_MIN,
                EvaluacionesLimits.ItemCualitativoAsesor.PESO_MAX,
                EvaluacionesFields.ItemCualitativoAsesor.PESO,
                EvaluacionesCodes.ItemCualitativoAsesor.PESO_FUERA_DE_RANGO,
                resultado)) {
            return;
        }
        this.peso = peso;
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

    public int getPeso() {
        return peso;
    }
}
