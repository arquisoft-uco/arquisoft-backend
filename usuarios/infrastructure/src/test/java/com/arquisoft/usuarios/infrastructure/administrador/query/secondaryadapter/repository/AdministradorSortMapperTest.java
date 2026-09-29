package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdministradorSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoCampoValido() {
        // Act & Assert
        assertThat(AdministradorSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(AdministradorSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(AdministradorSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoOrdenable() {
        // estado y vigente existen en el Criteria pero no son ordenables
        // Act & Assert
        assertThat(AdministradorSortMapper.traducir("estado")).isNull();
        assertThat(AdministradorSortMapper.traducir("vigente")).isNull();
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // Act & Assert
        assertThat(AdministradorSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : AdministradorCriteria.Campo.values()) {
            // Act
            var ruta = AdministradorSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(AdministradorCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
