package com.arquisoft.fichas.application.estadofichaperfil.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaPerfilAsesorCriteriaTest {

    @Test
    void debeMarcarLosCuatroCamposComoFiltrables_ySoloTituloProyectoComoOrdenable() {
        // Arrange & Act & Assert
        for (var campo : EstadoFichaPerfilAsesorCriteria.Campo.values()) {
            assertThat(EstadoFichaPerfilAsesorCriteria.Campo.esValidoParaFiltrar(campo.getClave())).isTrue();
        }
        assertThat(EstadoFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EstadoFichaPerfilAsesorCriteria.Campo.TITULO_PROYECTO.getClave())).isTrue();
        assertThat(EstadoFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EstadoFichaPerfilAsesorCriteria.Campo.FICHA_PERFIL.getClave())).isFalse();
        assertThat(EstadoFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EstadoFichaPerfilAsesorCriteria.Campo.ESTADO_FICHA.getClave())).isFalse();
        assertThat(EstadoFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EstadoFichaPerfilAsesorCriteria.Campo.ASESOR_FICHA.getClave())).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoElFiltroUsaUnCampoNoPermitido() {
        // Arrange
        var filtroInvalido = NodoFiltro.predicado("campoInexistente", FiltroOperador.ES, "valor");

        // Act & Assert
        assertThatThrownBy(() -> EstadoFichaPerfilAsesorCriteria.builder().raiz(filtroInvalido).build())
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElOrdenUsaUnCampoNoOrdenable() {
        // Arrange
        var ordenNoPermitido = List.of(SortOrder.of("fichaPerfil", SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> EstadoFichaPerfilAsesorCriteria.builder().ordenamiento(ordenNoPermitido).build())
                .isInstanceOf(FiltroException.class);
    }
}
