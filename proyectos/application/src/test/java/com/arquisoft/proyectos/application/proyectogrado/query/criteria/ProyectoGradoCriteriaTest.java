package com.arquisoft.proyectos.application.proyectogrado.query.criteria;

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

class ProyectoGradoCriteriaTest {

    @ParameterizedTest
    @ValueSource(strings = {"id", "tituloProyecto", "estadoProyectoGrado", "coordinador"})
    void debeAceptarCamposFiltrables_cuandoSeConstruyeElCriteria(String campo) {
        // Arrange
        var raiz = NodoFiltro.predicado(campo, FiltroOperador.ES, "valor");

        // Act
        var criteria = ProyectoGradoCriteria.builder().raiz(raiz).build();

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeRechazarFiltro_cuandoCampoNoPermitido() {
        // Arrange
        var raiz = NodoFiltro.predicado("fichaPerfil", FiltroOperador.ES, "valor");

        // Act & Assert
        assertThatThrownBy(() -> ProyectoGradoCriteria.builder().raiz(raiz))
                .isInstanceOf(FiltroException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"coordinador", "estadoProyectoGrado", "id"})
    void debeRechazarOrden_cuandoCampoNoOrdenable(String campo) {
        // Arrange
        var orden = List.of(SortOrder.of(campo, SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> ProyectoGradoCriteria.builder().ordenamiento(orden))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeAceptarOrden_cuandoCampoEsTituloProyecto() {
        // Arrange
        var orden = List.of(SortOrder.of("tituloProyecto", SortDirection.DESC));

        // Act
        var criteria = ProyectoGradoCriteria.builder().ordenamiento(orden).build();

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
    }
}
