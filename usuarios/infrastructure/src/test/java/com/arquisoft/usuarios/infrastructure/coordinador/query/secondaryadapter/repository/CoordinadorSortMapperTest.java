package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoCampoValido() {
        // Act & Assert
        assertThat(CoordinadorSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(CoordinadorSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(CoordinadorSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoOrdenable() {
        // estado y vigente existen en el Criteria pero no son ordenables
        // Act & Assert
        assertThat(CoordinadorSortMapper.traducir("estado")).isNull();
        assertThat(CoordinadorSortMapper.traducir("vigente")).isNull();
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // Act & Assert
        assertThat(CoordinadorSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : CoordinadorCriteria.Campo.values()) {
            // Act
            var ruta = CoordinadorSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(CoordinadorCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
