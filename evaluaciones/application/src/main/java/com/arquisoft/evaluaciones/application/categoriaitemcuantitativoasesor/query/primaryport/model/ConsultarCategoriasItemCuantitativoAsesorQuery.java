package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;

public record ConsultarCategoriasItemCuantitativoAsesorQuery(String nombre) {

    public static ConsultarCategoriasItemCuantitativoAsesorQuery crear(String nombreFiltro) {
        var nombreNormalizado = UtilTexto.aplicarTrim(nombreFiltro);

        if (UtilTexto.esVacioONulo(nombreNormalizado)) {
            return new ConsultarCategoriasItemCuantitativoAsesorQuery(null);
        }

        var resultado = new ValidationResult();

        ValidatorLongitud.longitudMaxima(
                nombreNormalizado,
                EvaluacionesLimits.CategoriaItemCuantitativoAsesor.NOMBRE_MAX,
                EvaluacionesFields.CategoriaItemCuantitativoAsesor.NOMBRE,
                EvaluacionesCodes.CategoriaItemCuantitativoAsesor.NOMBRE_FILTRO_DEMASIADO_LARGO,
                resultado);

        resultado.lanzarSiTieneErroresDeEntrada();

        return new ConsultarCategoriasItemCuantitativoAsesorQuery(nombreNormalizado);
    }
}
