package com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesDisponiblesQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilTexto;

import java.util.List;

public final class ConsultarEntregablesDisponiblesMapper {

    private ConsultarEntregablesDisponiblesMapper() {}

    public static EntregableDisponibleCriteria toCriteria(ConsultarEntregablesDisponiblesQuery query) {
        var criterio = query.criterio();

        var ordenamiento = List.of(
                SortOrder.of(EntregableDisponibleCriteria.Campo.TITULO_AUSENTE.getClave(), SortDirection.ASC),
                SortOrder.of(EntregableDisponibleCriteria.Campo.TITULO.getClave(), SortDirection.ASC),
                SortOrder.of(EntregableDisponibleCriteria.Campo.VERSION_ENTREGABLE.getClave(), SortDirection.ASC));

        var raiz = UtilTexto.esVacioONulo(query.titulo())
                ? null
                : NodoFiltro.predicado(
                        EntregableDisponibleCriteria.Campo.TITULO.getClave(), FiltroOperador.CONTIENE, query.titulo());

        return EntregableDisponibleCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(raiz)
                .build();
    }
}
