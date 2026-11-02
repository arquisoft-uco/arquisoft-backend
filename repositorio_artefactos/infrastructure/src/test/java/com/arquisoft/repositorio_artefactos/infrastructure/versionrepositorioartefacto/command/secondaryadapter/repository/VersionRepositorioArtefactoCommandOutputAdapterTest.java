package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class VersionRepositorioArtefactoCommandOutputAdapterTest {

    @Autowired
    private VersionRepositorioArtefactoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private VersionRepositorioArtefactoEntity version(UUID repositorio, String documento, int numero) {
        return new VersionRepositorioArtefactoEntity(UtilUUID.generarNuevoUUID(), documento, numero, Instant.now(),
                repositorio, "versiones/x", "application/pdf", 2048L, "formato.pdf", "a".repeat(64));
    }

    private VersionRepositorioArtefactoCommandOutputAdapter adapter() {
        return new VersionRepositorioArtefactoCommandOutputAdapter(repository, logger);
    }

    @Test
    void debeRegistrar() {
        // Arrange
        var entity = version(UtilUUID.generarNuevoUUID(), "Formato A", 1);

        // Act
        adapter().registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = repository.findById(entity.id()).orElseThrow();
        assertThat(guardado.getNombreDocumento()).isEqualTo("Formato A");
        assertThat(guardado.getNumeroVersion()).isEqualTo(1);
        assertThat(guardado.getRepositorioArtefacto()).isEqualTo(entity.repositorioArtefacto());
        assertThat(guardado.getUriArchivo()).isEqualTo("versiones/x");
        assertThat(guardado.getTipoContenido()).isEqualTo("application/pdf");
        assertThat(guardado.getTamanoBytes()).isEqualTo(2048L);
        assertThat(guardado.getNombreArchivo()).isEqualTo("formato.pdf");
        assertThat(guardado.getHashSha256()).isEqualTo("a".repeat(64));
        verify(logger).debug(VersionRepositorioArtefactoKey.LOG_GUARDADA, entity.id());
    }

    @Test
    void debeDevolverVacio_cuandoNoHayVersiones() {
        // Act & Assert
        assertThat(adapter().obtenerUltimaVersionDocumento(UtilUUID.generarNuevoUUID(), "Formato A")).isEmpty();
    }

    @Test
    void debeDevolverLaDeMayorNumero_cuandoHayVarias() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        adapter().registrar(version(repositorio, "Formato A", 1));
        adapter().registrar(version(repositorio, "Formato A", 2));
        adapter().registrar(version(repositorio, "Formato B", 7));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().obtenerUltimaVersionDocumento(repositorio, "Formato A"))
                .hasValueSatisfying(ultima -> assertThat(ultima.numeroVersion()).isEqualTo(2));
        assertThat(adapter().obtenerUltimaVersionDocumento(repositorio, "Formato B"))
                .hasValueSatisfying(ultima -> assertThat(ultima.numeroVersion()).isEqualTo(7));
    }

    @Test
    void debeEncontrarLaUltimaVersion_cuandoElNombreDifiereEnMayusculas() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        adapter().registrar(version(repositorio, "Plantilla A", 3));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().obtenerUltimaVersionDocumento(repositorio, "plantilla a"))
                .hasValueSatisfying(ultima -> {
                    assertThat(ultima.nombreDocumento()).isEqualTo("Plantilla A");
                    assertThat(ultima.numeroVersion()).isEqualTo(3);
                });
    }

    @Test
    void debeDevolverVacio_cuandoElDocumentoEsDeOtroRepositorio() {
        // Arrange
        var uno = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        adapter().registrar(version(uno, "Formato A", 5));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().obtenerUltimaVersionDocumento(otro, "Formato A")).isEmpty();
    }

    @Test
    void debeRechazar_numeroDuplicado() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        adapter().registrar(version(repositorio, "Formato A", 1));
        entityManager.flush();

        // Act & Assert
        assertThatThrownBy(() -> {
            adapter().registrar(version(repositorio, "Formato A", 1));
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }
}
