package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoAsesorQuery;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarProyectosGradoAsesorMapperTest {

    @Test
    void debeForzarAsesorYEstadoAsignadoYOrdenarPorTituloAsc_cuandoSinFiltrosNiOrden() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(2, 25, List.of(), null);
        var query = ConsultarProyectosGradoAsesorQuery.crear(asesor, criterio);
        var forzadoAsesor = NodoFiltro.predicado(
                ProyectoGradoAsesorCriteria.Campo.ASESOR.getClave(), FiltroOperador.ES, asesor.toString());
        var forzadoEstado = NodoFiltro.predicado(
                ProyectoGradoAsesorCriteria.Campo.ESTADO_ASESOR.getClave(), FiltroOperador.ES,
                EstadoAsesor.ASIGNADO.getId());

        // Act
        var criteria = ConsultarProyectosGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz())
                .isEqualTo(NodoFiltro.grupo(FiltroConector.AND, List.of(forzadoAsesor, forzadoEstado)));
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().getFirst().getCampo()).isEqualTo("tituloProyecto");
        assertThat(criteria.getOrdenamiento().getFirst().getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeCombinarEnAndYConservarOrden_cuandoElClienteEnviaRaizYOrden() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("estadoProyectoGrado", FiltroOperador.ES, "EN_PROCESO");
        var ordenamiento = List.of(SortOrder.of("tituloProyecto", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(0, 10, ordenamiento, raizCliente);
        var query = ConsultarProyectosGradoAsesorQuery.crear(asesor, criterio);
        var forzadoAsesor = NodoFiltro.predicado(
                ProyectoGradoAsesorCriteria.Campo.ASESOR.getClave(), FiltroOperador.ES, asesor.toString());
        var forzadoEstado = NodoFiltro.predicado(
                ProyectoGradoAsesorCriteria.Campo.ESTADO_ASESOR.getClave(), FiltroOperador.ES,
                EstadoAsesor.ASIGNADO.getId());

        // Act
        var criteria = ConsultarProyectosGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzadoAsesor, forzadoEstado, raizCliente)));
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().getFirst().getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeLanzarFiltroException_cuandoCampoNoPermitido() {
        // Arrange
        var raizCliente = NodoFiltro.predicado("campoInexistente", FiltroOperador.ES, "x");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);
        var query = ConsultarProyectosGradoAsesorQuery.crear(UtilUUID.generarNuevoUUID(), criterio);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarProyectosGradoAsesorMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }
}
