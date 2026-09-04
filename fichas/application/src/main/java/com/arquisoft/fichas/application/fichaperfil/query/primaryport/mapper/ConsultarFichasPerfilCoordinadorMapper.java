package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarFichasPerfilCoordinadorMapper {

    private ConsultarFichasPerfilCoordinadorMapper() {}

    public static FichaPerfilCriteria toCriteria(ConsultaCriteriaQuery query) {
        return FichaPerfilCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
