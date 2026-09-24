package com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadCoordinadorRecibidasQuery;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarSolicitudesNovedadCoordinadorRecibidasMapper {

    private ConsultarSolicitudesNovedadCoordinadorRecibidasMapper() {}

    public static SolicitudCriteria toCriteria(
            ConsultarSolicitudesNovedadCoordinadorRecibidasQuery query) {
        var criterio = query.criterio();

        var forzadoDestinatario = NodoFiltro.predicado(
                SolicitudCriteria.Campo.DESTINATARIO_USUARIO_ID.getClave(), FiltroOperador.ES,
                query.coordinadorUsuario().toString());

        var forzadoTipo = NodoFiltro.predicado(
                SolicitudCriteria.Campo.TIPO_SOLICITUD_ID.getClave(), FiltroOperador.ES,
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());

        var forzados = UtilObjeto.noEsNulo(criterio.raiz())
                ? List.of(forzadoDestinatario, forzadoTipo, criterio.raiz())
                : List.of(forzadoDestinatario, forzadoTipo);

        var raizFinal = NodoFiltro.grupo(FiltroConector.AND, forzados);

        return SolicitudCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento(criterio.ordenamiento()))
                .raiz(raizFinal)
                .build();
    }

    private static List<SortOrder> ordenamiento(List<SortOrder> solicitado) {
        if (!UtilColeccion.esVaciaONula(solicitado)) {
            return solicitado;
        }
        return List.of(SortOrder.of(
                SolicitudCriteria.Campo.FECHA_CREACION.getClave(), SortDirection.DESC));
    }
}
