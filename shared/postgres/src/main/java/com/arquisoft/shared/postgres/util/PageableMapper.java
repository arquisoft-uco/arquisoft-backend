package com.arquisoft.shared.postgres.util;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ConsultaKey;
import com.arquisoft.shared.pagination.SortDirection;
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
            // No es un error del cliente: el Criteria ya rechazó todo campo que no declare
            // ordenable, asi que llegar aqui significa que el SortMapper de la feature no
            // resuelve un campo que el Criteria si permite — las dos declaraciones divergieron.
            // Devolver 4xx le diria al cliente que su campo es invalido cuando no lo es; se deja
            // aflorar como 500 para que el defecto se vea.
            throw new IllegalStateException(
                    Mensajes.formatear(ConsultaKey.ERROR_RUTA_ORDEN_NO_MAPEADA, orden.getCampo()));
        }
        return orden.getDireccion() == SortDirection.ASC
                ? Sort.Order.asc(ruta)
                : Sort.Order.desc(ruta);
    }
}
