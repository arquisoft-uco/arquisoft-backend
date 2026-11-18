package com.arquisoft.biblioteca.application.documentacion.query.primaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarDocumentacionesQuery;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.ArrayList;
import java.util.List;

public final class ConsultarDocumentacionesMapper {

    private ConsultarDocumentacionesMapper() {}

    public static DocumentacionCriteria toCriteria(ConsultarDocumentacionesQuery query) {
        var criterio = query.criterio();

        var raiz = query.roles().contains(RolConsultaDocumentacion.BIBLIOTECARIO)
                ? criterio.raiz()
                : soloPublicadas(criterio.raiz());

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(SortOrder.of(
                        DocumentacionCriteria.Campo.FECHA_PUBLICACION.getClave(), SortDirection.DESC))
                : criterio.ordenamiento();

        return DocumentacionCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(raiz)
                .build();
    }

    private static NodoFiltro soloPublicadas(NodoFiltro raizCliente) {
        var forzado = NodoFiltro.predicado(
                DocumentacionCriteria.Campo.ESTADO_DOCUMENTACION.getClave(), FiltroOperador.ES,
                EstadoDocumentacion.PUBLICADO.getId());

        if (UtilObjeto.esNulo(raizCliente)) {
            return forzado;
        }

        var nodos = new ArrayList<NodoFiltro>();
        nodos.add(forzado);
        nodos.add(raizCliente);
        return NodoFiltro.grupo(FiltroConector.AND, nodos);
    }
}
