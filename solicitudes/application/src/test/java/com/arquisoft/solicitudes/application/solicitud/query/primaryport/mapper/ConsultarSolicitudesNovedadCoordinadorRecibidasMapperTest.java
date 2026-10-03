package com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadCoordinadorRecibidasQuery;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarSolicitudesNovedadCoordinadorRecibidasMapperTest {

    private static final String CLAVE_DESTINATARIO =
            SolicitudCriteria.Campo.DESTINATARIO_USUARIO_ID.getClave();
    private static final String CLAVE_TIPO = SolicitudCriteria.Campo.TIPO_SOLICITUD_ID.getClave();

    private static ConsultarSolicitudesNovedadCoordinadorRecibidasQuery query(
            UUID coordinador, ConsultaCriteriaQuery criterio) {
        return ConsultarSolicitudesNovedadCoordinadorRecibidasQuery.crear(coordinador, criterio);
    }

    @Test
    void debeForzarLosPredicadosDeDestinatarioYTipo_cuandoElCriterioNoTraeFiltros() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        SolicitudCriteria criteria = ConsultarSolicitudesNovedadCoordinadorRecibidasMapper.toCriteria(
                query(coordinador, criterio));

        // Assert
        var forzadoDestinatario = NodoFiltro.predicado(
                CLAVE_DESTINATARIO, FiltroOperador.ES, coordinador.toString());
        var forzadoTipo = NodoFiltro.predicado(
                CLAVE_TIPO, FiltroOperador.ES, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzadoDestinatario, forzadoTipo)));
    }

    @Test
    void debeCombinarConAndLosForzadosConElRaizDelCliente_cuandoElCriterioTraeUnFiltro() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var raizCliente = NodoFiltro.predicado("remitenteNombre", FiltroOperador.CONTIENE, "Ana");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);

        // Act
        SolicitudCriteria criteria = ConsultarSolicitudesNovedadCoordinadorRecibidasMapper.toCriteria(
                query(coordinador, criterio));

        // Assert
        var forzadoDestinatario = NodoFiltro.predicado(
                CLAVE_DESTINATARIO, FiltroOperador.ES, coordinador.toString());
        var forzadoTipo = NodoFiltro.predicado(
                CLAVE_TIPO, FiltroOperador.ES, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
        assertThat(criteria.getRaiz()).isEqualTo(NodoFiltro.grupo(FiltroConector.AND,
                List.of(forzadoDestinatario, forzadoTipo, raizCliente)));
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_sinModificarlos() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var ordenamiento = List.of(SortOrder.of("remitenteNombre", SortDirection.ASC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);

        // Act
        SolicitudCriteria criteria = ConsultarSolicitudesNovedadCoordinadorRecibidasMapper.toCriteria(
                query(coordinador, criterio));

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("remitenteNombre");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeAplicarOrdenPorFechaCreacionDescendente_cuandoElCriterioNoTraeOrden() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        SolicitudCriteria criteria = ConsultarSolicitudesNovedadCoordinadorRecibidasMapper.toCriteria(
                query(coordinador, criterio));

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("fechaCreacion");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeLanzarFiltroException_cuandoElClienteFiltraPorUnCampoFueraDeLaWhitelist() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var raizCliente = NodoFiltro.predicado("mensajeSolicitud", FiltroOperador.CONTIENE, "hola");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarSolicitudesNovedadCoordinadorRecibidasMapper.toCriteria(
                query(coordinador, criterio)))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElClienteOrdenaPorUnCampoNoOrdenable() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var ordenamiento = List.of(SortOrder.of("remitenteEmail", SortDirection.ASC));
        var criterio = ConsultaCriteriaQuery.crear(0, 10, ordenamiento, null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarSolicitudesNovedadCoordinadorRecibidasMapper.toCriteria(
                query(coordinador, criterio)))
                .isInstanceOf(FiltroException.class);
    }
}
