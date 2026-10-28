package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoEstudianteQuery;
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

class ConsultarEntregablesProyectoGradoEstudianteMapperTest {

    @Test
    void debeForzarElProyecto_cuandoNoHayRaiz() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var query = ConsultarEntregablesProyectoGradoEstudianteQuery.crear(
                UtilUUID.generarNuevoUUID(), proyecto, ConsultaCriteriaQuery.crear(0, 10, List.of(), null));

        // Act
        var criteria = ConsultarEntregablesProyectoGradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.consulta().getRaiz()).isEqualTo(
                NodoFiltro.predicado("proyectoGradoId", FiltroOperador.ES, proyecto.toString()));
    }

    @Test
    void debeCombinarConAnd_cuandoHayRaizDelCliente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.grupo(FiltroConector.OR, List.of(
                NodoFiltro.predicado("asesorNombre", FiltroOperador.CONTIENE, "Ana"),
                NodoFiltro.predicado("estadoEntregable", FiltroOperador.ES, "APROBADO")));
        var query = ConsultarEntregablesProyectoGradoEstudianteQuery.crear(
                UtilUUID.generarNuevoUUID(), proyecto, ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente));

        // Act
        var criteria = ConsultarEntregablesProyectoGradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.consulta().getRaiz()).isInstanceOfSatisfying(NodoFiltro.Grupo.class, grupo -> {
            assertThat(grupo.conector()).isEqualTo(FiltroConector.AND);
            assertThat(grupo.nodos()).containsExactly(
                    NodoFiltro.predicado("proyectoGradoId", FiltroOperador.ES, proyecto.toString()),
                    raizCliente);
        });
    }

    @Test
    void debeCopiarPaginaTamanioOrdenYLlevarEstudianteYProyecto() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var query = ConsultarEntregablesProyectoGradoEstudianteQuery.crear(estudiante, proyecto,
                ConsultaCriteriaQuery.crear(2, 25, List.of(SortOrder.of("asesorNombre", SortDirection.DESC)), null));

        // Act
        var criteria = ConsultarEntregablesProyectoGradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
        assertThat(criteria.proyectoGrado()).isEqualTo(proyecto);
        assertThat(criteria.consulta().getPagina()).isEqualTo(2);
        assertThat(criteria.consulta().getTamanio()).isEqualTo(25);
        assertThat(criteria.consulta().getOrdenamiento()).hasSize(1);
        assertThat(criteria.consulta().getOrdenamiento().get(0).getCampo()).isEqualTo("asesorNombre");
        assertThat(criteria.consulta().getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
