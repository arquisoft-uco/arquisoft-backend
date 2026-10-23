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

class EvaluacionAsesorCriteriaTest {

    @Test
    void debeDeclararAsesorIdYEntregableIdSoloFiltrables_yEstadoYProyectoFiltrablesYOrdenables() {
        // Act & Assert
        assertThat(EvaluacionAsesorCriteria.Campo.esValidoParaFiltrar("asesorId")).isTrue();
        assertThat(EvaluacionAsesorCriteria.Campo.esValidoParaOrdenar("asesorId")).isFalse();
        assertThat(EvaluacionAsesorCriteria.Campo.esValidoParaFiltrar("entregableId")).isTrue();
        assertThat(EvaluacionAsesorCriteria.Campo.esValidoParaOrdenar("entregableId")).isFalse();
        assertThat(EvaluacionAsesorCriteria.Campo.esValidoParaFiltrar("estado")).isTrue();
        assertThat(EvaluacionAsesorCriteria.Campo.esValidoParaOrdenar("estado")).isTrue();
        assertThat(EvaluacionAsesorCriteria.Campo.esValidoParaFiltrar("proyecto")).isTrue();
        assertThat(EvaluacionAsesorCriteria.Campo.esValidoParaOrdenar("proyecto")).isTrue();
    }

    @Test
    void debeLanzarFiltroException_cuandoSeFiltraPorUnCampoNoPermitido() {
        // Arrange
        var builder = EvaluacionAsesorCriteria.builder();
        var filtro = NodoFiltro.predicado("version", FiltroOperador.ES, "1");

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(filtro)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoSeOrdenaPorAsesorId() {
        // Arrange
        var builder = EvaluacionAsesorCriteria.builder();
        var orden = List.of(SortOrder.of("asesorId", SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(orden)).isInstanceOf(FiltroException.class);
    }
}
