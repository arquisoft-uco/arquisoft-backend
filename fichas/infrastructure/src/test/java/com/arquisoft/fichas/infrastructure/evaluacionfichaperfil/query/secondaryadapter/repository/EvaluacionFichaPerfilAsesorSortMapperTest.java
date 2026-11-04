package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionFichaPerfilAsesorSortMapperTest {

    @Test
    void debeTraducirLosCamposOrdenables_yRetornarNulo_paraLosNoOrdenablesYLosDesconocidos() {
        // Arrange & Act & Assert
        assertThat(EvaluacionFichaPerfilAsesorSortMapper.traducir("tituloProyecto")).isEqualTo("tituloProyecto");
        assertThat(EvaluacionFichaPerfilAsesorSortMapper.traducir("fechaCreacion")).isEqualTo("fechaCreacion");
        assertThat(EvaluacionFichaPerfilAsesorSortMapper.traducir("fichaPerfil")).isNull();
        assertThat(EvaluacionFichaPerfilAsesorSortMapper.traducir("estadoEvaluacion")).isNull();
        assertThat(EvaluacionFichaPerfilAsesorSortMapper.traducir("representanteComite")).isNull();
        assertThat(EvaluacionFichaPerfilAsesorSortMapper.traducir("asesorFicha")).isNull();
        assertThat(EvaluacionFichaPerfilAsesorSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : EvaluacionFichaPerfilAsesorCriteria.Campo.values()) {
            // Act
            var ruta = EvaluacionFichaPerfilAsesorSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(EvaluacionFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
