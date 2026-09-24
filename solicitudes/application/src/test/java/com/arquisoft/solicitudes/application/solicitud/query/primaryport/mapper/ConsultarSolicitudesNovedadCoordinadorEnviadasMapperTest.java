package com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadCoordinadorEnviadasQuery;
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

class ConsultarSolicitudesNovedadCoordinadorEnviadasMapperTest {

    private static final String CLAVE_REMITENTE =
            SolicitudCriteria.Campo.REMITENTE_USUARIO_ID.getClave();
    private static final String CLAVE_TIPO = SolicitudCriteria.Campo.TIPO_SOLICITUD_ID.getClave();

    private static ConsultarSolicitudesNovedadCoordinadorEnviadasQuery query(
            UUID estudiante, ConsultaCriteriaQuery criterio) {
        return ConsultarSolicitudesNovedadCoordinadorEnviadasQuery.crear(estudiante, criterio);
    }

    @Test
    void debeForzarLosPredicadosDeRemitenteYTipo_cuandoElCriterioNoTraeFiltros() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        SolicitudCriteria criteria = ConsultarSolicitudesNovedadCoordinadorEnviadasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        var forzadoRemitente = NodoFiltro.predicado(
                CLAVE_REMITENTE, FiltroOperador.ES, estudiante.toString());
        var forzadoTipo = NodoFiltro.predicado(
                CLAVE_TIPO, FiltroOperador.ES, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzadoRemitente, forzadoTipo)));
    }

    @Test
    void debeCombinarConAndLosForzadosConElRaizDelCliente_cuandoElCriterioTraeUnFiltro() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var raizCliente = NodoFiltro.predicado("destinatarioNombre", FiltroOperador.CONTIENE, "Ana");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);

        // Act
        SolicitudCriteria criteria = ConsultarSolicitudesNovedadCoordinadorEnviadasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        var forzadoRemitente = NodoFiltro.predicado(
                CLAVE_REMITENTE, FiltroOperador.ES, estudiante.toString());
        var forzadoTipo = NodoFiltro.predicado(
                CLAVE_TIPO, FiltroOperador.ES, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
        assertThat(criteria.getRaiz()).isEqualTo(NodoFiltro.grupo(FiltroConector.AND,
                List.of(forzadoRemitente, forzadoTipo, raizCliente)));
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_sinModificarlos() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var ordenamiento = List.of(SortOrder.of("destinatarioNombre", SortDirection.ASC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);

        // Act
        SolicitudCriteria criteria = ConsultarSolicitudesNovedadCoordinadorEnviadasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("destinatarioNombre");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeAplicarOrdenPorFechaCreacionDescendente_cuandoElCriterioNoTraeOrden() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        SolicitudCriteria criteria = ConsultarSolicitudesNovedadCoordinadorEnviadasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("fechaCreacion");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeForzarPorRemitenteUsuarioIdYNovedadCoordinador_yNoPorDestinatario() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        SolicitudCriteria criteria = ConsultarSolicitudesNovedadCoordinadorEnviadasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        var grupo = (NodoFiltro.Grupo) criteria.getRaiz();
        assertThat(grupo.nodos()).containsExactly(
                NodoFiltro.predicado(CLAVE_REMITENTE, FiltroOperador.ES, estudiante.toString()),
                NodoFiltro.predicado(CLAVE_TIPO, FiltroOperador.ES,
                        TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()));
        assertThat(grupo.nodos())
                .extracting(n -> ((NodoFiltro.Predicado) n).campo())
                .doesNotContain(SolicitudCriteria.Campo.DESTINATARIO_USUARIO_ID.getClave());
    }

    @Test
    void debeLanzarFiltroException_cuandoElClienteFiltraPorUnCampoFueraDeLaWhitelist() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var raizCliente = NodoFiltro.predicado("mensajeSolicitud", FiltroOperador.CONTIENE, "hola");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarSolicitudesNovedadCoordinadorEnviadasMapper.toCriteria(
                query(estudiante, criterio)))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElClienteOrdenaPorUnCampoNoOrdenable() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var ordenamiento = List.of(SortOrder.of("destinatarioEmail", SortDirection.ASC));
        var criterio = ConsultaCriteriaQuery.crear(0, 10, ordenamiento, null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarSolicitudesNovedadCoordinadorEnviadasMapper.toCriteria(
                query(estudiante, criterio)))
                .isInstanceOf(FiltroException.class);
    }
}
