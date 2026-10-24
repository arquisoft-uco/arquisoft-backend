package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarVersionesRepositorioArtefactoAsesorMapper {

    private ConsultarVersionesRepositorioArtefactoAsesorMapper() {}

    public static VersionRepositorioArtefactoAsesorCriteria toCriteria(ConsultaCriteriaQuery query) {
        var ordenamiento = UtilColeccion.esVaciaONula(query.ordenamiento())
                ? List.of(
                        SortOrder.of(VersionRepositorioArtefactoAsesorCriteria.Campo.NOMBRE_DOCUMENTO.getClave(),
                                SortDirection.ASC),
                        SortOrder.of(VersionRepositorioArtefactoAsesorCriteria.Campo.NUMERO_VERSION.getClave(),
                                SortDirection.DESC))
                : query.ordenamiento();

        return VersionRepositorioArtefactoAsesorCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(query.raiz())
                .build();
    }
}
