package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionRepositorioArtefactoAsesorSortMapperTest {

    @Test
    void debeTraducirClavesOrdenables_cuandoTraduce() {
        // Act & Assert
        assertThat(VersionRepositorioArtefactoAsesorSortMapper.traducir("nombreDocumento"))
                .isEqualTo("nombreDocumento");
        assertThat(VersionRepositorioArtefactoAsesorSortMapper.traducir("numeroVersion"))
                .isEqualTo("numeroVersion");
        assertThat(VersionRepositorioArtefactoAsesorSortMapper.traducir("fechaCreacion"))
                .isEqualTo("fechaCreacion");
        assertThat(VersionRepositorioArtefactoAsesorSortMapper.traducir("nombreRepositorio"))
                .isEqualTo("nombreRepositorio");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveNoEsOrdenable() {
        // Act & Assert
        assertThat(VersionRepositorioArtefactoAsesorSortMapper.traducir("tipoContenido")).isNull();
        assertThat(VersionRepositorioArtefactoAsesorSortMapper.traducir("repositorioId")).isNull();
        assertThat(VersionRepositorioArtefactoAsesorSortMapper.traducir("campoInventado")).isNull();
    }

    @Test
    void debeResolverRuta_soloParaLosCamposOrdenablesDelCriteria() {
        for (var campo : VersionRepositorioArtefactoAsesorCriteria.Campo.values()) {
            // Act
            var ruta = VersionRepositorioArtefactoAsesorSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(VersionRepositorioArtefactoAsesorCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as(campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
