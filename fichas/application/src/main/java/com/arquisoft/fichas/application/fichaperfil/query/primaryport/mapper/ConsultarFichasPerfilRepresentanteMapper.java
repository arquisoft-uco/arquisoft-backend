package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilRepresentanteQuery;

public final class ConsultarFichasPerfilRepresentanteMapper {

    private ConsultarFichasPerfilRepresentanteMapper() {}

    public static FichaPerfilRepresentanteCriteria toCriteria(
            ConsultarFichasPerfilRepresentanteQuery query) {
        var criterio = query.criterio();

        return FichaPerfilRepresentanteCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(criterio.raiz())
                .representanteComite(query.representanteComite())
                .build();
    }
}
