package com.arquisoft.fichas.application.fichaperfil.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilCriteriaTest {

    @ParameterizedTest
    @EnumSource(FiltroOperador.class)
    void debeAceptarFiltroEstado_cuandoCualquierOperador(FiltroOperador operador) {
        // Arrange
        var raiz = operador.esMultivalor()
                ? NodoFiltro.predicadoMultivalor("estadoFicha", operador, List.of("XYZ", "APROBADA"))
                : NodoFiltro.predicado("estadoFicha", operador, operador.requiereValor() ? "XYZ" : null);

        // Act & Assert
        assertThatCode(() -> FichaPerfilCriteria.builder().raiz(raiz).build()).doesNotThrowAnyException();
        assertThat(FichaPerfilCriteria.builder().raiz(raiz).build().getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeLanzarFiltroException_cuandoSeOrdenaPorEstadoFicha() {
        // Arrange
        var orden = List.of(SortOrder.of("estadoFicha", SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> FichaPerfilCriteria.builder().ordenamiento(orden))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeDeclararEstadoFichaFiltrableYNoOrdenable_cuandoSeConsultaElCampo() {
        // Act & Assert
        assertThat(FichaPerfilCriteria.Campo.esValidoParaFiltrar("estadoFicha")).isTrue();
        assertThat(FichaPerfilCriteria.Campo.esValidoParaOrdenar("estadoFicha")).isFalse();
    }
}
