package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoCampoValido() {
        // Act & Assert
        assertThat(AsesorSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(AsesorSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(AsesorSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoOrdenable() {
        // estado y vigente existen en el Criteria pero no son ordenables
        // Act & Assert
        assertThat(AsesorSortMapper.traducir("estado")).isNull();
        assertThat(AsesorSortMapper.traducir("vigente")).isNull();
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // Act & Assert
        assertThat(AsesorSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : AsesorCriteria.Campo.values()) {
            // Act
            var ruta = AsesorSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(AsesorCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
