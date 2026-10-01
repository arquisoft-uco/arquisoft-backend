package com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class DestinatarioCommandOutputAdapterTest {

    @Autowired
    private DestinatarioCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private DestinatarioCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DestinatarioCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debeInsertarLaFila_cuandoRegistra() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();

        // Act
        adapter.registrar(new DestinatarioEntity(id, usuario));
        entityManager.flush();
        entityManager.clear();

        // Assert
        DestinatarioJpaEntity guardada = entityManager.find(DestinatarioJpaEntity.class, id);
        assertThat(guardada).isNotNull();
        assertThat(guardada.getUsuarioId()).isEqualTo(usuario);
    }

    @Test
    void debeResolverElIdPorUsuario_cuandoLaFilaExisteOAusente() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();
        entityManager.persist(DestinatarioJpaEntity.builder().id(id).usuarioId(usuario).build());
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.buscarIdPorUsuario(usuario)).contains(id);
        assertThat(adapter.buscarIdPorUsuario(UUID.randomUUID())).isEmpty();
    }
}
