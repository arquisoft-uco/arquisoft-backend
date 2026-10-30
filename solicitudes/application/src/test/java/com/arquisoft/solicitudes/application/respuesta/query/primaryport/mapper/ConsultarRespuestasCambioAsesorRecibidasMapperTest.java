package com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasCambioAsesorRecibidasQuery;
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

class ConsultarRespuestasCambioAsesorRecibidasMapperTest {

    private static final String CLAVE_REMITENTE =
            RespuestaCriteria.Campo.REMITENTE_USUARIO_ID.getClave();
    private static final String CLAVE_TIPO = RespuestaCriteria.Campo.TIPO_SOLICITUD_ID.getClave();

    private static ConsultarRespuestasCambioAsesorRecibidasQuery query(
            UUID estudiante, ConsultaCriteriaQuery criterio) {
        return ConsultarRespuestasCambioAsesorRecibidasQuery.crear(estudiante, criterio);
    }

    @Test
    void debeForzarLosPredicadosDeRemitenteYTipo_cuandoElCriterioNoTraeFiltros() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarRespuestasCambioAsesorRecibidasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        var forzadoRemitente = NodoFiltro.predicado(
                CLAVE_REMITENTE, FiltroOperador.ES, estudiante.toString());
        var forzadoTipo = NodoFiltro.predicado(
                CLAVE_TIPO, FiltroOperador.ES, TipoSolicitud.CAMBIO_DE_ASESOR.getId());
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzadoRemitente, forzadoTipo)));
    }

    @Test
    void debeCombinarConAndLosForzadosConElRaizDelCliente_cuandoElCriterioTraeUnFiltro() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("destinatarioNombre", FiltroOperador.CONTIENE, "Ana");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);

        // Act
        var criteria = ConsultarRespuestasCambioAsesorRecibidasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        var forzadoRemitente = NodoFiltro.predicado(
                CLAVE_REMITENTE, FiltroOperador.ES, estudiante.toString());
        var forzadoTipo = NodoFiltro.predicado(
                CLAVE_TIPO, FiltroOperador.ES, TipoSolicitud.CAMBIO_DE_ASESOR.getId());
        assertThat(criteria.getRaiz()).isEqualTo(NodoFiltro.grupo(FiltroConector.AND,
                List.of(forzadoRemitente, forzadoTipo, raizCliente)));
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_cuandoElQueryLosTrae() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var ordenamiento = List.of(SortOrder.of("destinatarioNombre", SortDirection.ASC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);

        // Act
        var criteria = ConsultarRespuestasCambioAsesorRecibidasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("destinatarioNombre");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeAplicarOrdenPorFechaRespuestaDescendente_cuandoElCriterioNoTraeOrden() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarRespuestasCambioAsesorRecibidasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("fechaRespuesta");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeLanzarFiltroException_cuandoElClienteFiltraPorUnCampoFueraDeLaWhitelist() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("solicitudId", FiltroOperador.ES, UtilUUID.generarNuevoUUID().toString());
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarRespuestasCambioAsesorRecibidasMapper.toCriteria(
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
        assertThatThrownBy(() -> ConsultarRespuestasCambioAsesorRecibidasMapper.toCriteria(
                query(estudiante, criterio)))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeFijarElTipoSolicitudDelCriteria_cuandoSeConvierteLaConsulta() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarRespuestasCambioAsesorRecibidasMapper.toCriteria(
                query(estudiante, criterio));

        // Assert
        assertThat(criteria.getTipoSolicitud()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR.getId());
    }
}
