package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ArtefactoCommandOutputAdapterTest {

    @Autowired
    private ArtefactoCommandRepository artefactoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private ArtefactoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoCommandOutputAdapter(artefactoCommandRepository, logger);
    }

    @Test
    void debeGuardarTodosYLeerDeVuelta_cuandoSePersisteElLote() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var primero = new ArtefactoEntity(
                UtilUUID.generarNuevoUUID(), proyecto, UtilUUID.generarNuevoUUID(), asesor);
        var segundo = new ArtefactoEntity(
                UtilUUID.generarNuevoUUID(), proyecto, UtilUUID.generarNuevoUUID(), asesor);

        // Act
        adapter.registrarTodos(List.of(primero, segundo));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(ArtefactoJpaEntity.class, segundo.id());
        assertThat(guardado.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(guardado.getVersionRepositorioArtefacto()).isEqualTo(segundo.versionRepositorioArtefacto());
        assertThat(guardado.getAsesor()).isEqualTo(asesor);
        assertThat(entityManager.find(ArtefactoJpaEntity.class, primero.id())).isNotNull();
        verify(logger).debug(ArtefactoKey.LOG_GUARDADOS, 2);
    }

}
