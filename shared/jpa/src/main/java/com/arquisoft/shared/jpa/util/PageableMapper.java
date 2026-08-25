package com.arquisoft.shared.jpa.util;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ConsultaKey;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.query.QueryCriteria;
import com.arquisoft.shared.query.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.UnaryOperator;

public final class PageableMapper {

    private PageableMapper() {}

    public static Pageable toPageable(QueryCriteria criteria, UnaryOperator<String> traductorDeCampo) {
        if (!criteria.tieneOrden()) {
            return PageRequest.of(criteria.getPagina(), criteria.getTamanio());
        }
        List<Sort.Order> ordenes = criteria.getOrdenamiento().stream()
                .map(orden -> aSortOrder(orden, traductorDeCampo))
                .toList();
        return PageRequest.of(criteria.getPagina(), criteria.getTamanio(), Sort.by(ordenes));
    }

    private static Sort.Order aSortOrder(SortOrder orden, UnaryOperator<String> traductorDeCampo) {
        String ruta = traductorDeCampo.apply(orden.getCampo());
        if (ruta == null) {
            throw new IllegalStateException(
                    Mensajes.formatear(ConsultaKey.ERROR_RUTA_ORDEN_NO_MAPEADA, orden.getCampo()));
        }
        return orden.getDireccion() == SortDirection.ASC
                ? Sort.Order.asc(ruta)
                : Sort.Order.desc(ruta);
    }
}
