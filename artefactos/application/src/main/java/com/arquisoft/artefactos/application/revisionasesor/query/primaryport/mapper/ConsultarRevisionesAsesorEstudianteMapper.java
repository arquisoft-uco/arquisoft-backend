package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorEstudianteCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorEstudianteQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarRevisionesAsesorEstudianteMapper {

    private ConsultarRevisionesAsesorEstudianteMapper() {}

    public static RevisionAsesorEstudianteCriteria toCriteria(ConsultarRevisionesAsesorEstudianteQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                RevisionAsesorEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES,
                query.estudiante().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return RevisionAsesorEstudianteCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
