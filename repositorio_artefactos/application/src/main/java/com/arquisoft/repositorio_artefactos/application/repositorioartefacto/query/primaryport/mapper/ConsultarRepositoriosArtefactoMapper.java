package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarRepositoriosArtefactoMapper {

    private ConsultarRepositoriosArtefactoMapper() {}

    public static RepositorioArtefactoCriteria toCriteria(ConsultaCriteriaQuery query) {
        return RepositorioArtefactoCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
