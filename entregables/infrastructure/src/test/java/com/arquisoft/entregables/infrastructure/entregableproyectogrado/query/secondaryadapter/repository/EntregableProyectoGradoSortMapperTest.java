package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoSortMapperTest {

    @Test
    void debeTraducirLosCamposOrdenables() {
        // Act & Assert
        assertThat(EntregableProyectoGradoSortMapper.traducir("versionEntregable")).isEqualTo("versionEntregable");
        assertThat(EntregableProyectoGradoSortMapper.traducir("proyectoGradoNombre")).isEqualTo("proyectoGradoNombre");
        assertThat(EntregableProyectoGradoSortMapper.traducir("asesorNombre")).isEqualTo("asesorNombre");
    }

    @Test
    void debeRetornarNull_cuandoElCampoNoEsOrdenable() {
        // Act & Assert
        assertThat(EntregableProyectoGradoSortMapper.traducir("proyectoGradoId")).isNull();
        assertThat(EntregableProyectoGradoSortMapper.traducir("asesorId")).isNull();
        assertThat(EntregableProyectoGradoSortMapper.traducir("estadoEntregable")).isNull();
        assertThat(EntregableProyectoGradoSortMapper.traducir("inexistente")).isNull();
    }

    @Test
    void debeResolverRuta_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : EntregableProyectoGradoCriteria.Campo.values()) {
            // Act
            var ruta = EntregableProyectoGradoSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(EntregableProyectoGradoCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("campo %s", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
