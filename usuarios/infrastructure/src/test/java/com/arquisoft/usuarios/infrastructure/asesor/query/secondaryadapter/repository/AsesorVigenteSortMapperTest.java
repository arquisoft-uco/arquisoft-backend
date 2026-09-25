package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorVigenteSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoCampoValido() {
        // Act & Assert
        assertThat(AsesorVigenteSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(AsesorVigenteSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(AsesorVigenteSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // estado/vigente no existen en este Criteria: no hay ruta que puedan resolver
        // Act & Assert
        assertThat(AsesorVigenteSortMapper.traducir("estado")).isNull();
        assertThat(AsesorVigenteSortMapper.traducir("vigente")).isNull();
        assertThat(AsesorVigenteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : AsesorVigenteCriteria.Campo.values()) {
            // Act
            var ruta = AsesorVigenteSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(AsesorVigenteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
