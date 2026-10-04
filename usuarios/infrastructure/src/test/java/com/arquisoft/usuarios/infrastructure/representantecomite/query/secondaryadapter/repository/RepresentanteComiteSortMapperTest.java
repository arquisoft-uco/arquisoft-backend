package com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepresentanteComiteSortMapperTest {

    @Test
    void debeTraducirIdentificadorNombreYEmail_cuandoElCampoEsOrdenable() {
        // Act & Assert
        assertThat(RepresentanteComiteSortMapper.traducir("identificador")).isEqualTo("identificador");
        assertThat(RepresentanteComiteSortMapper.traducir("nombre")).isEqualTo("nombre");
        assertThat(RepresentanteComiteSortMapper.traducir("email")).isEqualTo("email");
    }

    @Test
    void debeRetornarNull_cuandoElCampoNoEsOrdenableONoExiste() {
        // Act & Assert
        assertThat(RepresentanteComiteSortMapper.traducir("estado")).isNull();
        assertThat(RepresentanteComiteSortMapper.traducir("vigente")).isNull();
        assertThat(RepresentanteComiteSortMapper.traducir("contacto")).isNull();
        assertThat(RepresentanteComiteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeResolverRutaJpa_soloParaLosCamposQueElCriteriaDeclaraOrdenables() {
        for (var campo : RepresentanteComiteCriteria.Campo.values()) {
            // Act
            var ruta = RepresentanteComiteSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(RepresentanteComiteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
