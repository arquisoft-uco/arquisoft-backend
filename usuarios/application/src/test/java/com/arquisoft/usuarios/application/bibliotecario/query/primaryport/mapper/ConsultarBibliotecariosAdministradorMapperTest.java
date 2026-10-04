package com.arquisoft.usuarios.application.bibliotecario.query.primaryport.mapper;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarBibliotecariosAdministradorMapperTest {

    @Test
    void debeConstruirCriteria_cuandoQueryValido() {
        // Arrange
        var raiz = NodoFiltro.predicado("email", FiltroOperador.CONTIENE, "@uco.edu.co");
        var query = ConsultaCriteriaQuery.crear(
                2, 25, List.of(SortOrder.of("nombre", SortDirection.DESC)), raiz);

        // Act
        var criteria = ConsultarBibliotecariosAdministradorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("nombre");
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeAceptarFiltros_cuandoFiltraPorVigenteYEstado() {
        // Arrange
        var raizVigente = NodoFiltro.predicado("vigente", FiltroOperador.ES, "true");
        var raizEstado = NodoFiltro.predicado("estado", FiltroOperador.ES, "INACTIVO");

        // Act
        var criteriaVigente = ConsultarBibliotecariosAdministradorMapper.toCriteria(
                ConsultaCriteriaQuery.crear(0, 10, List.of(), raizVigente));
        var criteriaEstado = ConsultarBibliotecariosAdministradorMapper.toCriteria(
                ConsultaCriteriaQuery.crear(0, 10, List.of(), raizEstado));

        // Assert
        assertThat(criteriaVigente.getRaiz()).isEqualTo(raizVigente);
        assertThat(criteriaEstado.getRaiz()).isEqualTo(raizEstado);
    }

    @Test
    void debeLanzarFiltroException_cuandoCampoNoFiltrable() {
        // Arrange
        var raiz = NodoFiltro.predicado("contacto", FiltroOperador.ES, "3000000000");
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);

        // Act
        var lanzamiento = assertThatThrownBy(() -> ConsultarBibliotecariosAdministradorMapper.toCriteria(query));

        // Assert
        lanzamiento.isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorVigente() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("vigente", SortDirection.ASC)), null);

        // Act
        var lanzamiento = assertThatThrownBy(() -> ConsultarBibliotecariosAdministradorMapper.toCriteria(query));

        // Assert
        lanzamiento.isInstanceOf(FiltroException.class);
    }
}
