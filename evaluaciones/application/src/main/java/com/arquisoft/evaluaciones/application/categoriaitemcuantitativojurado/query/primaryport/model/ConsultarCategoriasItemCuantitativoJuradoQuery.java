package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;

public record ConsultarCategoriasItemCuantitativoJuradoQuery(String nombre) {

    public static ConsultarCategoriasItemCuantitativoJuradoQuery crear(String nombreFiltro) {
        var nombreNormalizado = UtilTexto.aplicarTrim(nombreFiltro);

        if (UtilTexto.esVacioONulo(nombreNormalizado)) {
            return new ConsultarCategoriasItemCuantitativoJuradoQuery(null);
        }

        var resultado = new ValidationResult();

        ValidatorLongitud.longitudMaxima(
                nombreNormalizado,
                EvaluacionesLimits.CategoriaItemCuantitativoJurado.NOMBRE_MAX,
                EvaluacionesFields.CategoriaItemCuantitativoJurado.NOMBRE,
                EvaluacionesCodes.CategoriaItemCuantitativoJurado.NOMBRE_FILTRO_DEMASIADO_LARGO,
                resultado);

        resultado.lanzarSiTieneErroresDeEntrada();

        return new ConsultarCategoriasItemCuantitativoJuradoQuery(nombreNormalizado);
    }
}
