package com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosAsesorQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarArtefactosAsesorMapper {

    private ConsultarArtefactosAsesorMapper() {}

    public static ArtefactoAsesorCriteria toCriteria(ConsultarArtefactosAsesorQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                ArtefactoAsesorCriteria.Campo.ASESOR_ID.getClave(), FiltroOperador.ES,
                query.asesor().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(
                        SortOrder.of(ArtefactoAsesorCriteria.Campo.NOMBRE_PROYECTO.getClave(), SortDirection.ASC),
                        SortOrder.of(ArtefactoAsesorCriteria.Campo.NOMBRE_DOCUMENTO.getClave(), SortDirection.ASC))
                : criterio.ordenamiento();

        return ArtefactoAsesorCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(raizFinal)
                .build();
    }
}
