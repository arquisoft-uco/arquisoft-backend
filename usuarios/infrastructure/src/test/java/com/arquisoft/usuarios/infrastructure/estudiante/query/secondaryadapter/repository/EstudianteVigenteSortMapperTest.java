package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteVigenteCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteVigenteSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoCampoValido() {
        // Act & Assert
        assertThat(EstudianteVigenteSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(EstudianteVigenteSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(EstudianteVigenteSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoEsOrdenableONoExiste() {
        // estado es solo filtrable y vigente no existe en este Criteria: ninguno tiene ruta de orden
        // Act & Assert
        assertThat(EstudianteVigenteSortMapper.traducir("estado")).isNull();
        assertThat(EstudianteVigenteSortMapper.traducir("vigente")).isNull();
        assertThat(EstudianteVigenteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : EstudianteVigenteCriteria.Campo.values()) {
            // Act
            var ruta = EstudianteVigenteSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(EstudianteVigenteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
