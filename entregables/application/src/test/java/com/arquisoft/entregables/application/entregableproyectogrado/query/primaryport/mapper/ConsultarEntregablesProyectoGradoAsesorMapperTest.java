package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

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

class ConsultarEntregablesProyectoGradoAsesorMapperTest {

    @Test
    void debeCopiarPaginaTamanioOrdenYRaiz() {
        // Arrange
        var raiz = NodoFiltro.predicado("asesorNombre", FiltroOperador.CONTIENE, "Ana");
        var query = ConsultaCriteriaQuery.crear(
                2, 25, List.of(SortOrder.of("proyectoGradoNombre", SortDirection.DESC)), raiz);

        // Act
        var criteria = ConsultarEntregablesProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("proyectoGradoNombre");
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeConstruirSinFiltrosNiOrden_cuandoLaQueryNoLosTrae() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarEntregablesProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.tieneFiltros()).isFalse();
        assertThat(criteria.tieneOrden()).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoLaQueryUsaUnCampoNoPermitido() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("asesorId", SortDirection.ASC)), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarEntregablesProyectoGradoAsesorMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }
}
