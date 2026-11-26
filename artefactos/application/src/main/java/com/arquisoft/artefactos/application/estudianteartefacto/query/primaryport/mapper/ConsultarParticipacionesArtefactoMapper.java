package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarParticipacionesArtefactoMapper {

    private ConsultarParticipacionesArtefactoMapper() {}

    public static ParticipacionArtefactoCriteria toCriteria(ConsultarParticipacionesArtefactoQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                ParticipacionArtefactoCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES,
                query.estudiante().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(
                        SortOrder.of(ParticipacionArtefactoCriteria.Campo.NOMBRE_PROYECTO.getClave(), SortDirection.ASC),
                        SortOrder.of(ParticipacionArtefactoCriteria.Campo.NOMBRE_DOCUMENTO.getClave(), SortDirection.ASC))
                : criterio.ordenamiento();

        return ParticipacionArtefactoCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(raizFinal)
                .build();
    }
}
