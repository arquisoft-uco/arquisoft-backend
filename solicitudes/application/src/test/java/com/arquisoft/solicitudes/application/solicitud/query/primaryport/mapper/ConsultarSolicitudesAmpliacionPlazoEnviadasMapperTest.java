package com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesAmpliacionPlazoEnviadasQuery;
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

class ConsultarSolicitudesAmpliacionPlazoEnviadasMapperTest {

    private static final String CLAVE_REMITENTE =
            SolicitudCriteria.Campo.REMITENTE_USUARIO_ID.getClave();
    private static final String CLAVE_TIPO = SolicitudCriteria.Campo.TIPO_SOLICITUD_ID.getClave();

    private static ConsultarSolicitudesAmpliacionPlazoEnviadasQuery query(
            UUID estudiante, ConsultaCriteriaQuery criterio) {
        return ConsultarSolicitudesAmpliacionPlazoEnviadasQuery.crear(estudiante, criterio);
    }

    @Test
    void debeForzarLosPredicadosDeRemitenteYTipo_cuandoElCriterioNoTraeFiltros() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarSolicitudesAmpliacionPlazoEnviadasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        var forzadoRemitente = NodoFiltro.predicado(
                CLAVE_REMITENTE, FiltroOperador.ES, estudiante.toString());
        var forzadoTipo = NodoFiltro.predicado(
                CLAVE_TIPO, FiltroOperador.ES, TipoSolicitud.AMPLIACION_DE_PLAZO.getId());
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzadoRemitente, forzadoTipo)));
    }

    @Test
    void debeCombinarConAndLosForzadosConElRaizDelCliente_cuandoElCriterioTraeUnFiltro() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("destinatarioNombre", FiltroOperador.CONTIENE, "Coord");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);

        // Act
        var criteria = ConsultarSolicitudesAmpliacionPlazoEnviadasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        var forzadoRemitente = NodoFiltro.predicado(
                CLAVE_REMITENTE, FiltroOperador.ES, estudiante.toString());
        var forzadoTipo = NodoFiltro.predicado(
                CLAVE_TIPO, FiltroOperador.ES, TipoSolicitud.AMPLIACION_DE_PLAZO.getId());
        assertThat(criteria.getRaiz()).isEqualTo(NodoFiltro.grupo(FiltroConector.AND,
                List.of(forzadoRemitente, forzadoTipo, raizCliente)));
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_sinModificarlos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var ordenamiento = List.of(SortOrder.of("destinatarioNombre", SortDirection.ASC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);

        // Act
        var criteria = ConsultarSolicitudesAmpliacionPlazoEnviadasMapper.toCriteria(
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
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarSolicitudesAmpliacionPlazoEnviadasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("fechaCreacion");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeLanzarFiltroException_cuandoElClienteFiltraPorUnCampoFueraDeLaWhitelist() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("mensajeSolicitud", FiltroOperador.CONTIENE, "hola");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarSolicitudesAmpliacionPlazoEnviadasMapper.toCriteria(
                query(estudiante, criterio)))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElClienteOrdenaPorUnCampoNoOrdenable() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var ordenamiento = List.of(SortOrder.of("destinatarioEmail", SortDirection.ASC));
        var criterio = ConsultaCriteriaQuery.crear(0, 10, ordenamiento, null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarSolicitudesAmpliacionPlazoEnviadasMapper.toCriteria(
                query(estudiante, criterio)))
                .isInstanceOf(FiltroException.class);
    }
}
