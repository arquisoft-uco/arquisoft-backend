package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class VersionArtefactoCommandOutputAdapterTest {

    @Autowired
    private VersionArtefactoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private VersionArtefactoCommandOutputAdapter adapter() {
        return new VersionArtefactoCommandOutputAdapter(repository, logger);
    }

    private static VersionArtefactoEntity version(UUID artefacto, int numero) {
        var id = UtilUUID.generarNuevoUUID();
        return new VersionArtefactoEntity(id, artefacto, numero, Instant.now(), "versiones/" + id);
    }

    @Test
    void debeRegistrar() {
        // Arrange
        var entity = version(UtilUUID.generarNuevoUUID(), 1);

        // Act
        adapter().registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = repository.findById(entity.id()).orElseThrow();
        assertThat(guardado.getArtefacto()).isEqualTo(entity.artefacto());
        assertThat(guardado.getNumeroVersion()).isEqualTo(1);
        assertThat(guardado.getUriArchivo()).isEqualTo(entity.uriArchivo());
        verify(logger).debug(VersionArtefactoKey.LOG_GUARDADA, entity.id());
    }

    @Test
    void debeRetornarCero_cuandoSinVersiones() {
        // Act & Assert
        assertThat(adapter().ultimoNumeroPorArtefacto(UtilUUID.generarNuevoUUID())).isZero();
    }

    @Test
    void debeRetornarUltimoNumero_porArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        adapter().registrar(version(artefacto, 1));
        adapter().registrar(version(artefacto, 2));
        adapter().registrar(version(otro, 7));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().ultimoNumeroPorArtefacto(artefacto)).isEqualTo(2);
        assertThat(adapter().ultimoNumeroPorArtefacto(otro)).isEqualTo(7);
    }
}
