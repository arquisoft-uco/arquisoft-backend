package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
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

class ConsultarFichasPerfilCoordinadorMapperTest {

    @Test
    void debePropagarPaginaTamanioOrdenamientoYRaiz_cuandoElCriterioEsValido() {
        // Arrange
        var raiz = NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "web");
        var query = ConsultaCriteriaQuery.crear(
                2, 25, List.of(SortOrder.of("asesorNombre", SortDirection.DESC)), raiz);

        // Act
        FichaPerfilCriteria criteria = ConsultarFichasPerfilCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("asesorNombre");
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeConstruirCriteriaSinFiltros_cuandoElCriterioNoTraeRaiz() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        FichaPerfilCriteria criteria = ConsultarFichasPerfilCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.tieneFiltros()).isFalse();
        assertThat(criteria.tieneOrden()).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoLaRaizUsaUnCampoNoFiltrable() {
        // Arrange
        var raiz = NodoFiltro.predicado("campoInventado", FiltroOperador.ES, "x");
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarFichasPerfilCoordinadorMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElOrdenamientoUsaUnCampoNoOrdenable() {
        // Arrange — asesorId es filtrable pero no ordenable
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("asesorId", SortDirection.ASC)), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarFichasPerfilCoordinadorMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }
}
