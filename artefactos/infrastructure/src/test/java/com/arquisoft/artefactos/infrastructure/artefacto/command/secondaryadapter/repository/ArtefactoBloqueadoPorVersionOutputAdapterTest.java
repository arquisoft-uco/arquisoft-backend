package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class ArtefactoBloqueadoPorVersionOutputAdapterTest {

    @Autowired
    private ArtefactoCommandRepository artefactoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ArtefactoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoCommandOutputAdapter(artefactoCommandRepository, mock(AppLogger.class));
    }

    private UUID sembrarArtefacto() {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(id)
                .proyectoGrado(UtilUUID.generarNuevoUUID())
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
        return id;
    }

    @Test
    void debeObtenerBloqueadoPorVersion_cuandoLaVersionExiste() {
        // Arrange
        var artefacto = sembrarArtefacto();
        var otro = sembrarArtefacto();
        var version = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(version, artefacto, 1));
        entityManager.persist(new VersionArtefactoTestEntity(UtilUUID.generarNuevoUUID(), otro, 1));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerBloqueadoPorVersionArtefacto(version);

        // Assert
        assertThat(resultado).hasValueSatisfying(encontrado -> assertThat(encontrado.id()).isEqualTo(artefacto));
    }

    @Test
    void debeDevolverVacio_cuandoLaVersionNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerBloqueadoPorVersionArtefacto(UtilUUID.generarNuevoUUID())).isEmpty();
    }
}
