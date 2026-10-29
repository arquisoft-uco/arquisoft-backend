package com.arquisoft.entregables.application.entregableproyectogrado.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableProyectoGradoCriteriaTest {

    @Test
    void debeConstruir_cuandoLosCamposSonFiltrablesYOrdenables() {
        // Arrange
        var raiz = NodoFiltro.predicado("estadoEntregable", FiltroOperador.ES, "APROBADO");
        var orden = List.of(SortOrder.of("versionEntregable", SortDirection.DESC));

        // Act
        var criteria = EntregableProyectoGradoCriteria.builder().raiz(raiz).ordenamiento(orden).build();

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
        assertThat(criteria.tieneFiltros()).isTrue();
        assertThat(criteria.tieneOrden()).isTrue();
        assertThat(EntregableProyectoGradoCriteria.Campo.PROYECTO_GRADO_ID.getClave()).isEqualTo("proyectoGradoId");
    }

    @Test
    void debeRechazar_cuandoElCampoDeFiltroNoExiste() {
        // Arrange
        var raiz = NodoFiltro.predicado("campoInventado", FiltroOperador.ES, "x");

        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoCriteria.builder().raiz(raiz))
                .isInstanceOf(FiltroException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"proyectoGradoId", "asesorId", "estadoEntregable"})
    void debeRechazar_cuandoElCampoDeOrdenNoEsOrdenable(String campo) {
        // Arrange
        var orden = List.of(SortOrder.of(campo, SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoCriteria.builder().ordenamiento(orden))
                .isInstanceOf(FiltroException.class);
        assertThat(EntregableProyectoGradoCriteria.Campo.esValidoParaFiltrar(campo)).isTrue();
        assertThat(EntregableProyectoGradoCriteria.Campo.esValidoParaOrdenar(campo)).isFalse();
    }
}
