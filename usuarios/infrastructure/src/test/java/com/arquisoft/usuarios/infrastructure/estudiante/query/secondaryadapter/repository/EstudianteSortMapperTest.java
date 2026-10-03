package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoCampoValido() {
        // Act & Assert
        assertThat(EstudianteSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(EstudianteSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(EstudianteSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoOrdenable() {
        // estado y vigente existen en el Criteria pero no son ordenables
        // Act & Assert
        assertThat(EstudianteSortMapper.traducir("estado")).isNull();
        assertThat(EstudianteSortMapper.traducir("vigente")).isNull();
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // Act & Assert
        assertThat(EstudianteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : EstudianteCriteria.Campo.values()) {
            // Act
            var ruta = EstudianteSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(EstudianteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
