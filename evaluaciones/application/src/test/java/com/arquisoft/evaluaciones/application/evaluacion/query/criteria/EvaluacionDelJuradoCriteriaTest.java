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

class EvaluacionDelJuradoCriteriaTest {

    @Test
    void debeDeclararJuradoIdYEntregableIdSoloFiltrables_yEstadoYProyectoFiltrablesYOrdenables() {
        // Act & Assert
        assertThat(EvaluacionDelJuradoCriteria.Campo.esValidoParaFiltrar("juradoId")).isTrue();
        assertThat(EvaluacionDelJuradoCriteria.Campo.esValidoParaOrdenar("juradoId")).isFalse();
        assertThat(EvaluacionDelJuradoCriteria.Campo.esValidoParaFiltrar("entregableId")).isTrue();
        assertThat(EvaluacionDelJuradoCriteria.Campo.esValidoParaOrdenar("entregableId")).isFalse();
        assertThat(EvaluacionDelJuradoCriteria.Campo.esValidoParaFiltrar("estado")).isTrue();
        assertThat(EvaluacionDelJuradoCriteria.Campo.esValidoParaOrdenar("estado")).isTrue();
        assertThat(EvaluacionDelJuradoCriteria.Campo.esValidoParaFiltrar("proyecto")).isTrue();
        assertThat(EvaluacionDelJuradoCriteria.Campo.esValidoParaOrdenar("proyecto")).isTrue();
    }

    @Test
    void debeLanzarFiltroException_cuandoSeFiltraPorUnCampoNoPermitido() {
        // Arrange
        var builder = EvaluacionDelJuradoCriteria.builder();
        var filtro = NodoFiltro.predicado("version", FiltroOperador.ES, "1");

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(filtro)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoSeOrdenaPorJuradoId() {
        // Arrange
        var builder = EvaluacionDelJuradoCriteria.builder();
        var orden = List.of(SortOrder.of("juradoId", SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(orden)).isInstanceOf(FiltroException.class);
    }
}
