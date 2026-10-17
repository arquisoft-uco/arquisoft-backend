package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemJuradoSortMapperTest {

    @Test
    void debeTraducirDescripcion_aSuRutaEnLaEntidad() {
        // Act & Assert
        assertThat(ObservacionItemJuradoSortMapper.traducir(
                ObservacionItemJuradoCriteria.Campo.DESCRIPCION.getClave())).isEqualTo("descripcion");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveNoEsOrdenable() {
        // Act & Assert
        assertThat(ObservacionItemJuradoSortMapper.traducir("evaluacionCuantitativaJurado")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : ObservacionItemJuradoCriteria.Campo.values()) {
            // Act
            var ruta = ObservacionItemJuradoSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(ObservacionItemJuradoCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
