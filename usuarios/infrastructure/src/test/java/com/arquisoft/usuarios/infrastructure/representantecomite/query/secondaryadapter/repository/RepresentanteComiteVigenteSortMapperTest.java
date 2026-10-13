package com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteVigenteCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepresentanteComiteVigenteSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoElCampoEsOrdenable() {
        // Act & Assert
        assertThat(RepresentanteComiteVigenteSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(RepresentanteComiteVigenteSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(RepresentanteComiteVigenteSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoElCampoNoEsOrdenableONoExiste() {
        // Act & Assert
        assertThat(RepresentanteComiteVigenteSortMapper.traducir("estado")).isNull();
        assertThat(RepresentanteComiteVigenteSortMapper.traducir("vigente")).isNull();
        assertThat(RepresentanteComiteVigenteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverRutaJpa_soloParaLosCamposQueElCriteriaDeclaraOrdenables() {
        for (var campo : RepresentanteComiteVigenteCriteria.Campo.values()) {
            // Act
            var ruta = RepresentanteComiteVigenteSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(RepresentanteComiteVigenteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
