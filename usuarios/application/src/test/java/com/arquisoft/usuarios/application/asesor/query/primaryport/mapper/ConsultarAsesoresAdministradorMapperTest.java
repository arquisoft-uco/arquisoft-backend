package com.arquisoft.usuarios.application.asesor.query.primaryport.mapper;

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

class ConsultarAsesoresAdministradorMapperTest {

    @Test
    void debePropagarPaginaTamanioOrdenamientoYRaiz_cuandoElCriterioEsValido() {
        // Arrange
        var raiz = NodoFiltro.predicado("nombre", FiltroOperador.CONTIENE, "ana");
        var query = ConsultaCriteriaQuery.crear(
                2, 25, List.of(SortOrder.of("identificador", SortDirection.DESC)), raiz);

        // Act
        var criteria = ConsultarAsesoresAdministradorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("identificador");
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeAceptarUnFiltroSobreVigenteYEstado_puesSonExclusivosDelAdministrador() {
        // Arrange
        var raizVigente = NodoFiltro.predicado("vigente", FiltroOperador.ES, "false");
        var raizEstado = NodoFiltro.predicado("estado", FiltroOperador.ES, "INACTIVO");

        // Act
        var criteriaVigente = ConsultarAsesoresAdministradorMapper.toCriteria(
                ConsultaCriteriaQuery.crear(0, 10, List.of(), raizVigente));
        var criteriaEstado = ConsultarAsesoresAdministradorMapper.toCriteria(
                ConsultaCriteriaQuery.crear(0, 10, List.of(), raizEstado));

        // Assert
        assertThat(criteriaVigente.getRaiz()).isEqualTo(raizVigente);
        assertThat(criteriaEstado.getRaiz()).isEqualTo(raizEstado);
    }

    @Test
    void debeLanzarFiltroException_cuandoLaRaizUsaUnCampoNoFiltrable() {
        // Arrange
        var raiz = NodoFiltro.predicado("campoInventado", FiltroOperador.ES, "x");
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarAsesoresAdministradorMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElOrdenamientoUsaUnCampoNoOrdenable() {
        // Arrange — vigente es filtrable pero no ordenable
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("vigente", SortDirection.ASC)), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarAsesoresAdministradorMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeConstruirCriteriaSinFiltros_cuandoElCriterioNoTraeRaiz() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarAsesoresAdministradorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.tieneFiltros()).isFalse();
        assertThat(criteria.tieneOrden()).isFalse();
    }
}
