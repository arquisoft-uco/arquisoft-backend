package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaVigenteSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoElCampoEsOrdenable() {
        // Act & Assert
        assertThat(AsesorFichaVigenteSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(AsesorFichaVigenteSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(AsesorFichaVigenteSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoElCampoNoEsOrdenableONoExiste() {
        // Act & Assert
        assertThat(AsesorFichaVigenteSortMapper.traducir("estado")).isNull();
        assertThat(AsesorFichaVigenteSortMapper.traducir("vigente")).isNull();
        assertThat(AsesorFichaVigenteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverRutaJpa_soloParaLosCamposQueElCriteriaDeclaraOrdenables() {
        for (var campo : AsesorFichaVigenteCriteria.Campo.values()) {
            // Act
            var ruta = AsesorFichaVigenteSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(AsesorFichaVigenteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
