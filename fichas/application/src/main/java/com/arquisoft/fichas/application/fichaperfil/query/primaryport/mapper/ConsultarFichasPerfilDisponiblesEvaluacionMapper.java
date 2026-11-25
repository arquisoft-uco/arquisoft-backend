package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilDisponiblesEvaluacionQuery;

public final class ConsultarFichasPerfilDisponiblesEvaluacionMapper {

    private ConsultarFichasPerfilDisponiblesEvaluacionMapper() {}

    public static FichaPerfilDisponibleEvaluacionCriteria toCriteria(
            ConsultarFichasPerfilDisponiblesEvaluacionQuery query) {
        var criterio = query.criterio();

        return FichaPerfilDisponibleEvaluacionCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(criterio.raiz())
                .representanteComite(query.representanteComite())
                .build();
    }
}
