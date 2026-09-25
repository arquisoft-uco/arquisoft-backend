package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorVigenteSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoCampoValido() {
        // Act & Assert
        assertThat(CoordinadorVigenteSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(CoordinadorVigenteSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(CoordinadorVigenteSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // estado/vigente no existen en este Criteria: no hay ruta que puedan resolver
        // Act & Assert
        assertThat(CoordinadorVigenteSortMapper.traducir("estado")).isNull();
        assertThat(CoordinadorVigenteSortMapper.traducir("vigente")).isNull();
        assertThat(CoordinadorVigenteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : CoordinadorVigenteCriteria.Campo.values()) {
            // Act
            var ruta = CoordinadorVigenteSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(CoordinadorVigenteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
