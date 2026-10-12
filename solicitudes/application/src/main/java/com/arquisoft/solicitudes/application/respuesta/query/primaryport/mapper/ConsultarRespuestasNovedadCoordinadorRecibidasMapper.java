package com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadCoordinadorRecibidasQuery;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarRespuestasNovedadCoordinadorRecibidasMapper {

    private ConsultarRespuestasNovedadCoordinadorRecibidasMapper() {}

    public static RespuestaCriteria toCriteria(
            ConsultarRespuestasNovedadCoordinadorRecibidasQuery query) {
        var criterio = query.criterio();

        var forzadoRemitente = NodoFiltro.predicado(
                RespuestaCriteria.Campo.REMITENTE_USUARIO_ID.getClave(), FiltroOperador.ES,
                query.estudianteUsuario().toString());

        var forzadoTipo = NodoFiltro.predicado(
                RespuestaCriteria.Campo.TIPO_SOLICITUD_ID.getClave(), FiltroOperador.ES,
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());

        var forzados = UtilObjeto.noEsNulo(criterio.raiz())
                ? List.of(forzadoRemitente, forzadoTipo, criterio.raiz())
                : List.of(forzadoRemitente, forzadoTipo);

        var raizFinal = NodoFiltro.grupo(FiltroConector.AND, forzados);

        return RespuestaCriteria.builder()
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
                RespuestaCriteria.Campo.FECHA_RESPUESTA.getClave(), SortDirection.DESC));
    }
}
