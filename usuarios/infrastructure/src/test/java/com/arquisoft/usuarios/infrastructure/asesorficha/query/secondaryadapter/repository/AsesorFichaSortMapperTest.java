package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoElCampoEsOrdenable() {
        // Act & Assert
        assertThat(AsesorFichaSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(AsesorFichaSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(AsesorFichaSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoElCampoNoEsOrdenableONoExiste() {
        // Act & Assert
        assertThat(AsesorFichaSortMapper.traducir("estado")).isNull();
        assertThat(AsesorFichaSortMapper.traducir("vigente")).isNull();
        assertThat(AsesorFichaSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverRutaJpa_soloParaLosCamposQueElCriteriaDeclaraOrdenables() {
        for (var campo : AsesorFichaCriteria.Campo.values()) {
            // Act
            var ruta = AsesorFichaSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(AsesorFichaCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
