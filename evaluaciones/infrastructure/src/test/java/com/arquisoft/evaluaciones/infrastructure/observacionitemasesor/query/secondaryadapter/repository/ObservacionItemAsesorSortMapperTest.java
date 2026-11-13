package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemAsesorSortMapperTest {

    @Test
    void debeTraducirDescripcion_aSuRutaEnLaEntidad() {
        // Act & Assert
        assertThat(ObservacionItemAsesorSortMapper.traducir(
                ObservacionItemAsesorCriteria.Campo.DESCRIPCION.getClave())).isEqualTo("descripcion");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveNoEsOrdenable() {
        // Act & Assert
        assertThat(ObservacionItemAsesorSortMapper.traducir("evaluacionCuantitativaAsesor")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : ObservacionItemAsesorCriteria.Campo.values()) {
            // Act
            var ruta = ObservacionItemAsesorSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(ObservacionItemAsesorCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
