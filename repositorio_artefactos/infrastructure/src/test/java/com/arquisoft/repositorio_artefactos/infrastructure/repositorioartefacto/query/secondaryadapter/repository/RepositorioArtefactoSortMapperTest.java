package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepositorioArtefactoSortMapperTest {

    @Test
    void debeTraducirClavesOrdenables_cuandoTraduce() {
        // Act & Assert
        assertThat(RepositorioArtefactoSortMapper.traducir("nombreRepositorio")).isEqualTo("nombreRepositorio");
        assertThat(RepositorioArtefactoSortMapper.traducir("descripcion")).isEqualTo("descripcion");
        assertThat(RepositorioArtefactoSortMapper.traducir("administradorNombre")).isEqualTo("administradorNombre");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveNoEsOrdenable() {
        // Act & Assert
        assertThat(RepositorioArtefactoSortMapper.traducir("administradorId")).isNull();
        assertThat(RepositorioArtefactoSortMapper.traducir("administradorIdentificador")).isNull();
        assertThat(RepositorioArtefactoSortMapper.traducir("campoInventado")).isNull();
    }

    @Test
    void debeResolverRuta_soloParaLosCamposOrdenablesDelCriteria() {
        for (var campo : RepositorioArtefactoCriteria.Campo.values()) {
            // Act
            var ruta = RepositorioArtefactoSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(RepositorioArtefactoCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as(campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
