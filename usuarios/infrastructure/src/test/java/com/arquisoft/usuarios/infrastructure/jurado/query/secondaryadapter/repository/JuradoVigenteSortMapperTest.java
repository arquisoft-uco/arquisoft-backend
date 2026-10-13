package com.arquisoft.usuarios.infrastructure.jurado.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JuradoVigenteSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoCampoValido() {
        // Act & Assert
        assertThat(JuradoVigenteSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(JuradoVigenteSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(JuradoVigenteSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoEsOrdenableONoExiste() {
        // estado es solo filtrable y vigente no existe en este Criteria: ninguno tiene ruta de orden
        // Act & Assert
        assertThat(JuradoVigenteSortMapper.traducir("estado")).isNull();
        assertThat(JuradoVigenteSortMapper.traducir("vigente")).isNull();
        assertThat(JuradoVigenteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : JuradoVigenteCriteria.Campo.values()) {
            // Act
            var ruta = JuradoVigenteSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(JuradoVigenteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
