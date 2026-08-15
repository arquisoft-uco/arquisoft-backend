package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.shared.web.dto.query.QueryCriteriaRequestDTO;

public final class ConsultarFichasPerfilRequestMapper {

    private ConsultarFichasPerfilRequestMapper() {}

    public static FichaPerfilCriteria toCriteria(QueryCriteriaRequestDTO dto) {
        QueryCriteriaRequestDTO solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        return FichaPerfilCriteria.builder()
                .pagina(solicitud.getPagina())
                .tamanio(solicitud.getTamanio())
                .ordenamiento(solicitud.parsearOrdenamiento())
                .raiz(solicitud.parsearFiltros())
                .build();
    }
}
