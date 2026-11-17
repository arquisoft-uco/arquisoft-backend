package com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.message.constant.BibliotecaLimits;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.QueryCriteria;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;

import java.util.List;

public record ConsultarEntregablesDisponiblesQuery(
        ConsultaCriteriaQuery criterio,
        String titulo
) {

    public static ConsultarEntregablesDisponiblesQuery crear(int pagina, int tamanio, String titulo) {
        var result = new ValidationResult();
        var tituloRecortado = UtilTexto.aplicarTrim(titulo);

        ValidatorNumero.valorMinimo(pagina, 0,
                BibliotecaFields.EntregableProyectoGrado.PAGINA,
                BibliotecaCodes.EntregableProyectoGrado.PAGINA_INVALIDA, result);
        ValidatorNumero.valorEntre(tamanio, 1, QueryCriteria.MAX_TAMANIO,
                BibliotecaFields.EntregableProyectoGrado.TAMANIO,
                BibliotecaCodes.EntregableProyectoGrado.TAMANIO_INVALIDO, result);
        ValidatorLongitud.longitudMaxima(tituloRecortado, BibliotecaLimits.ProyectoGrado.TITULO_MAX,
                BibliotecaFields.ProyectoGrado.TITULO,
                BibliotecaCodes.EntregableProyectoGrado.TITULO_LONGITUD_EXCEDIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEntregablesDisponiblesQuery(
                ConsultaCriteriaQuery.crear(pagina, tamanio, List.of(), null), tituloRecortado);
    }
}
