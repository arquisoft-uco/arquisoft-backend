package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoEstudianteQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarVersionesArtefactoEstudianteMapper {

    private ConsultarVersionesArtefactoEstudianteMapper() {}

    public static VersionArtefactoEstudianteCriteria toCriteria(ConsultarVersionesArtefactoEstudianteQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                VersionArtefactoEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES,
                query.estudiante().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(SortOrder.of(VersionArtefactoEstudianteCriteria.Campo.FECHA_CREACION.getClave(),
                        SortDirection.DESC))
                : criterio.ordenamiento();

        return VersionArtefactoEstudianteCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(raizFinal)
                .build();
    }
}
