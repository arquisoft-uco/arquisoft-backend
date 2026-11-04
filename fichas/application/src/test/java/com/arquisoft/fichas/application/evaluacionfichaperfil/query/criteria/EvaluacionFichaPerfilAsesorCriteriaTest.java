package com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionFichaPerfilAsesorCriteriaTest {

    @Test
    void debeMarcarLosSeisCamposComoFiltrables_ySoloTituloProyectoYFechaCreacionComoOrdenables() {
        // Arrange & Act & Assert
        for (var campo : EvaluacionFichaPerfilAsesorCriteria.Campo.values()) {
            assertThat(EvaluacionFichaPerfilAsesorCriteria.Campo.esValidoParaFiltrar(campo.getClave())).isTrue();
        }
        assertThat(EvaluacionFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EvaluacionFichaPerfilAsesorCriteria.Campo.TITULO_PROYECTO.getClave())).isTrue();
        assertThat(EvaluacionFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EvaluacionFichaPerfilAsesorCriteria.Campo.FECHA_CREACION.getClave())).isTrue();
        assertThat(EvaluacionFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EvaluacionFichaPerfilAsesorCriteria.Campo.FICHA_PERFIL.getClave())).isFalse();
        assertThat(EvaluacionFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EvaluacionFichaPerfilAsesorCriteria.Campo.ESTADO_EVALUACION.getClave())).isFalse();
        assertThat(EvaluacionFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EvaluacionFichaPerfilAsesorCriteria.Campo.REPRESENTANTE_COMITE.getClave())).isFalse();
        assertThat(EvaluacionFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(
                EvaluacionFichaPerfilAsesorCriteria.Campo.ASESOR_FICHA.getClave())).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoElFiltroUsaUnCampoNoPermitido() {
        // Arrange
        var filtroInvalido = NodoFiltro.predicado("campoInexistente", FiltroOperador.ES, "valor");

        // Act & Assert
        assertThatThrownBy(() -> EvaluacionFichaPerfilAsesorCriteria.builder().raiz(filtroInvalido).build())
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElOrdenUsaUnCampoNoOrdenable() {
        // Arrange
        var ordenNoPermitido = List.of(SortOrder.of("estadoEvaluacion", SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> EvaluacionFichaPerfilAsesorCriteria.builder().ordenamiento(ordenNoPermitido).build())
                .isInstanceOf(FiltroException.class);
    }
}
