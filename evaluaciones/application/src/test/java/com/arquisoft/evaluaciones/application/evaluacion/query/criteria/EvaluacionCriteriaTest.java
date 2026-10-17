package com.arquisoft.evaluaciones.application.evaluacion.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCriteriaTest {

    @Test
    void debeDeclararEstadoYProyectoFiltrablesYOrdenables_yEntregableIdSoloFiltrable() {
        // Act & Assert
        assertThat(EvaluacionCriteria.Campo.esValidoParaFiltrar("estado")).isTrue();
        assertThat(EvaluacionCriteria.Campo.esValidoParaOrdenar("estado")).isTrue();

        assertThat(EvaluacionCriteria.Campo.esValidoParaFiltrar("proyecto")).isTrue();
        assertThat(EvaluacionCriteria.Campo.esValidoParaOrdenar("proyecto")).isTrue();

        assertThat(EvaluacionCriteria.Campo.esValidoParaFiltrar("entregableId")).isTrue();
        assertThat(EvaluacionCriteria.Campo.esValidoParaOrdenar("entregableId")).isFalse();
    }

    @Test
    void noDebeDeclararVersion_niComoFiltroNiComoOrden() {
        // Act & Assert
        assertThat(EvaluacionCriteria.Campo.esValidoParaFiltrar("version")).isFalse();
        assertThat(EvaluacionCriteria.Campo.esValidoParaOrdenar("version")).isFalse();
    }

    @Test
    void debeConstruirElCriteria_cuandoFiltroYOrdenSonPermitidos() {
        // Act
        var criteria = EvaluacionCriteria.builder()
                .pagina(1)
                .tamanio(5)
                .ordenamiento(List.of(SortOrder.of("estado", SortDirection.DESC)))
                .raiz(NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot"))
                .build();

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(1);
        assertThat(criteria.getTamanio()).isEqualTo(5);
        assertThat(criteria.tieneFiltros()).isTrue();
        assertThat(criteria.tieneOrden()).isTrue();
    }

    @Test
    void debeLanzarFiltroException_cuandoSeFiltraPorVersion() {
        // Arrange
        var builder = EvaluacionCriteria.builder();
        var filtro = NodoFiltro.predicado("version", FiltroOperador.ES, "1");

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(filtro)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoSeOrdenaPorEntregableId() {
        // Arrange
        var builder = EvaluacionCriteria.builder();
        var orden = List.of(SortOrder.of("entregableId", SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(orden)).isInstanceOf(FiltroException.class);
    }
}
