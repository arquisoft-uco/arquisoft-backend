package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesDisponiblesQuery;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto.ConsultarEntregablesDisponiblesRequestDTO;
import com.arquisoft.shared.util.UtilObjeto;

public final class ConsultarEntregablesDisponiblesRequestMapper {

    private static final int PAGINA_POR_DEFECTO = 0;
    private static final int TAMANIO_POR_DEFECTO = 10;

    private ConsultarEntregablesDisponiblesRequestMapper() {}

    public static ConsultarEntregablesDisponiblesQuery toQuery(ConsultarEntregablesDisponiblesRequestDTO dto) {
        var solicitud = UtilObjeto.aplicarPorDefecto(
                dto, new ConsultarEntregablesDisponiblesRequestDTO(null, null, null));

        return ConsultarEntregablesDisponiblesQuery.crear(
                UtilObjeto.aplicarPorDefecto(solicitud.pagina(), PAGINA_POR_DEFECTO),
                UtilObjeto.aplicarPorDefecto(solicitud.tamanio(), TAMANIO_POR_DEFECTO),
                solicitud.titulo());
    }
}
