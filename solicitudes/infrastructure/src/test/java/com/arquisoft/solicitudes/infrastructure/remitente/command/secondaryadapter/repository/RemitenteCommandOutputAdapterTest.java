package com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class RemitenteCommandOutputAdapterTest {

    @Autowired
    private RemitenteCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private RemitenteCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RemitenteCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debeInsertarLaFila_cuandoRegistra() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();

        // Act
        adapter.registrar(new RemitenteEntity(id, usuario));
        entityManager.flush();
        entityManager.clear();

        // Assert
        RemitenteJpaEntity guardada = entityManager.find(RemitenteJpaEntity.class, id);
        assertThat(guardada).isNotNull();
        assertThat(guardada.getUsuarioId()).isEqualTo(usuario);
    }

    @Test
    void debeRetornarElId_cuandoElUsuarioTieneFila() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();
        entityManager.persist(RemitenteJpaEntity.builder().id(id).usuarioId(usuario).build());
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.buscarIdPorUsuario(usuario)).contains(id);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoTieneFila() {
        // Act & Assert
        assertThat(adapter.buscarIdPorUsuario(UUID.randomUUID())).isEmpty();
    }
}
