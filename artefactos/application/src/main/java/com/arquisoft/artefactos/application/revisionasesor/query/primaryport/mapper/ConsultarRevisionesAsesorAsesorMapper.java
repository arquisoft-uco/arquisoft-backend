package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorAsesorCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorAsesorQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarRevisionesAsesorAsesorMapper {

    private ConsultarRevisionesAsesorAsesorMapper() {}

    public static RevisionAsesorAsesorCriteria toCriteria(ConsultarRevisionesAsesorAsesorQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                RevisionAsesorAsesorCriteria.Campo.ASESOR_ID.getClave(), FiltroOperador.ES,
                query.asesor().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(SortOrder.of(RevisionAsesorAsesorCriteria.Campo.ESTADO_REVISION_ASESOR.getClave(),
                        SortDirection.ASC))
                : criterio.ordenamiento();

        return RevisionAsesorAsesorCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(raizFinal)
                .build();
    }
}
