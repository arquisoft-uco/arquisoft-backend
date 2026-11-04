package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoCoordinadorQuery;
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

class ConsultarProyectosGradoCoordinadorMapperTest {

    @Test
    void debeForzarFiltroCoordinador_cuandoNoHayRaiz() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarProyectosGradoCoordinadorQuery.crear(coordinador, criterio);

        // Act
        var criteria = ConsultarProyectosGradoCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(NodoFiltro.predicado(
                ProyectoGradoCriteria.Campo.COORDINADOR.getClave(), FiltroOperador.ES, coordinador.toString()));
    }

    @Test
    void debeCombinarEnAnd_cuandoHayRaiz() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("estadoProyectoGrado", FiltroOperador.ES, "EN_PROCESO");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);
        var query = ConsultarProyectosGradoCoordinadorQuery.crear(coordinador, criterio);
        var forzado = NodoFiltro.predicado(
                ProyectoGradoCriteria.Campo.COORDINADOR.getClave(), FiltroOperador.ES, coordinador.toString());

        // Act
        var criteria = ConsultarProyectosGradoCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
    }

    @Test
    void debeCopiarPaginaTamanioYOrden_cuandoSeConvierte() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var ordenamiento = List.of(SortOrder.of("tituloProyecto", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);
        var query = ConsultarProyectosGradoCoordinadorQuery.crear(coordinador, criterio);

        // Act
        var criteria = ConsultarProyectosGradoCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().getFirst().getCampo()).isEqualTo("tituloProyecto");
        assertThat(criteria.getOrdenamiento().getFirst().getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
