package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VersionRepositorioArtefactoQueryOutputAdapterTest {

    @Autowired
    private VersionRepositorioArtefactoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private VersionRepositorioArtefactoQueryOutputAdapter adapter() {
        return new VersionRepositorioArtefactoQueryOutputAdapter(repository);
    }

    private UUID sembrar(UUID repositorio, String documento, int numero, String hash) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(VersionRepositorioArtefactoJpaEntity.builder()
                .id(id)
                .nombreDocumento(documento)
                .numeroVersion(numero)
                .fechaCreacion(Instant.parse("2026-10-20T10:00:00Z"))
                .repositorioArtefacto(repositorio)
                .uriArchivo("versiones/" + id)
                .tipoContenido("application/pdf")
                .tamanoBytes(2048L)
                .nombreArchivo("formato.pdf")
                .hashSha256(hash)
                .build());
        return id;
    }

    @Test
    void debeOrdenarPorNumeroVersionDescendente_cuandoHayVarias() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var v1 = sembrar(repositorio, "Formato A", 1, null);
        var v3 = sembrar(repositorio, "Formato A", 3, null);
        var v2 = sembrar(repositorio, "Formato A", 2, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(repositorio);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(v3, v2, v1);
    }

    @Test
    void debeDesempatarPorNombreDocumentoAscendente_cuandoMismoNumero() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var b = sembrar(repositorio, "Formato B", 2, null);
        var a = sembrar(repositorio, "Formato A", 2, null);
        var alto = sembrar(repositorio, "Formato Z", 5, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(repositorio);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(alto, a, b);
    }

    @Test
    void debeExcluirVersionesDeOtroRepositorio_cuandoHayVarios() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var propia = sembrar(repositorio, "Formato A", 1, null);
        sembrar(UtilUUID.generarNuevoUUID(), "Formato A", 1, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(repositorio);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(propia);
    }

    @Test
    void debeRetornarListaVacia_cuandoRepositorioNoTieneVersiones() {
        // Arrange
        sembrar(UtilUUID.generarNuevoUUID(), "Formato A", 1, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeMapearTodosLosCampos_cuandoHashEsNulo() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var id = sembrar(repositorio, "Formato A", 4, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(repositorio);

        // Assert
        assertThat(resultado).hasSize(1);
        var version = resultado.get(0);
        assertThat(version.id()).isEqualTo(id);
        assertThat(version.nombreDocumento()).isEqualTo("Formato A");
        assertThat(version.numeroVersion()).isEqualTo(4);
        assertThat(version.fechaCreacion()).isEqualTo(Instant.parse("2026-10-20T10:00:00Z"));
        assertThat(version.tipoContenido()).isEqualTo("application/pdf");
        assertThat(version.tamanoBytes()).isEqualTo(2048L);
        assertThat(version.nombreArchivo()).isEqualTo("formato.pdf");
        assertThat(version.hashSha256()).isNull();
    }
}
