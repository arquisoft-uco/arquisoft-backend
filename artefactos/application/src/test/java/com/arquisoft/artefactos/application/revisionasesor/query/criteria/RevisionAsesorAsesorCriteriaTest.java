package com.arquisoft.artefactos.application.revisionasesor.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorAsesorCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        assertThat(RevisionAsesorAsesorCriteria.Campo.esValidoParaFiltrar("versionArtefacto")).isTrue();
        assertThat(RevisionAsesorAsesorCriteria.Campo.esValidoParaOrdenar("versionArtefacto")).isFalse();

        assertThat(RevisionAsesorAsesorCriteria.Campo.esValidoParaFiltrar("estadoRevisionAsesor")).isTrue();
        assertThat(RevisionAsesorAsesorCriteria.Campo.esValidoParaOrdenar("estadoRevisionAsesor")).isTrue();

        assertThat(RevisionAsesorAsesorCriteria.Campo.esValidoParaFiltrar("asesorId")).isTrue();
        assertThat(RevisionAsesorAsesorCriteria.Campo.esValidoParaOrdenar("asesorId")).isFalse();

        assertThat(RevisionAsesorAsesorCriteria.Campo.esValidoParaFiltrar("artefacto")).isFalse();
        assertThat(RevisionAsesorAsesorCriteria.Campo.esValidoParaOrdenar("campoInexistente")).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoDeclarado() {
        // Arrange
        var raiz = NodoFiltro.predicado("artefacto", FiltroOperador.ES, "valor");
        var builder = RevisionAsesorAsesorCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorCampoNoOrdenable() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("versionArtefacto", SortDirection.ASC));
        var builder = RevisionAsesorAsesorCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }
}
