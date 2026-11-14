package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorVigenteCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdministradorVigenteSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoCampoValido() {
        // Act & Assert
        assertThat(AdministradorVigenteSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(AdministradorVigenteSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(AdministradorVigenteSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoEsOrdenableONoExiste() {
        // estado es solo filtrable y vigente no existe en este Criteria: ninguno tiene ruta de orden
        // Act & Assert
        assertThat(AdministradorVigenteSortMapper.traducir("estado")).isNull();
        assertThat(AdministradorVigenteSortMapper.traducir("vigente")).isNull();
        assertThat(AdministradorVigenteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : AdministradorVigenteCriteria.Campo.values()) {
            // Act
            var ruta = AdministradorVigenteSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(AdministradorVigenteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
