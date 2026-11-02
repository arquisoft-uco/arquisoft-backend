package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VersionRepositorioArtefactoAccesoQueryOutputAdapterTest {

    @Autowired
    private ArchivoVersionQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeDevolverUriYNombre_cuandoLaVersionExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(VersionRepositorioArtefactoJpaEntity.builder()
                .id(id)
                .nombreDocumento("Formato A")
                .numeroVersion(1)
                .fechaCreacion(Instant.parse("2026-10-20T10:00:00Z"))
                .repositorioArtefacto(UtilUUID.generarNuevoUUID())
                .uriArchivo("versiones/" + id)
                .tipoContenido("application/pdf")
                .tamanoBytes(2048L)
                .nombreArchivo("formato.pdf")
                .build());
        entityManager.flush();
        entityManager.clear();
        var adaptador = new VersionRepositorioArtefactoAccesoQueryOutputAdapter(repository);

        // Act
        var resultado = adaptador.obtenerArchivoPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().uriArchivo()).isEqualTo("versiones/" + id);
        assertThat(resultado.get().nombreArchivo()).isEqualTo("formato.pdf");
    }

    @Test
    void debeDevolverVacio_cuandoLaVersionNoExiste() {
        // Arrange
        var adaptador = new VersionRepositorioArtefactoAccesoQueryOutputAdapter(repository);

        // Act
        var resultado = adaptador.obtenerArchivoPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
