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

class EvaluacionEstudianteCriteriaTest {

    @Test
    void debeDeclararEstudianteIdYEntregableIdSoloFiltrables_yEstadoYProyectoFiltrablesYOrdenables() {
        // Act & Assert
        assertThat(EvaluacionEstudianteCriteria.Campo.esValidoParaFiltrar("estudianteId")).isTrue();
        assertThat(EvaluacionEstudianteCriteria.Campo.esValidoParaOrdenar("estudianteId")).isFalse();
        assertThat(EvaluacionEstudianteCriteria.Campo.esValidoParaFiltrar("entregableId")).isTrue();
        assertThat(EvaluacionEstudianteCriteria.Campo.esValidoParaOrdenar("entregableId")).isFalse();
        assertThat(EvaluacionEstudianteCriteria.Campo.esValidoParaFiltrar("estado")).isTrue();
        assertThat(EvaluacionEstudianteCriteria.Campo.esValidoParaOrdenar("estado")).isTrue();
        assertThat(EvaluacionEstudianteCriteria.Campo.esValidoParaFiltrar("proyecto")).isTrue();
        assertThat(EvaluacionEstudianteCriteria.Campo.esValidoParaOrdenar("proyecto")).isTrue();
    }

    @Test
    void debeLanzarFiltroException_cuandoSeFiltraPorUnCampoNoPermitido() {
        // Arrange
        var builder = EvaluacionEstudianteCriteria.builder();
        var filtro = NodoFiltro.predicado("version", FiltroOperador.ES, "1");

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(filtro)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoSeOrdenaPorEstudianteId() {
        // Arrange
        var builder = EvaluacionEstudianteCriteria.builder();
        var orden = List.of(SortOrder.of("estudianteId", SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(orden)).isInstanceOf(FiltroException.class);
    }
}
