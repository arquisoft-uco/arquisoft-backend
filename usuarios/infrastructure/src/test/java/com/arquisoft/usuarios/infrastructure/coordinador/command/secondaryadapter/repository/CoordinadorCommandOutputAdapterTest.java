package com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarCoordinadorKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class CoordinadorCommandOutputAdapterTest {

    @Autowired
    private CoordinadorCommandRepository coordinadorCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private UUID sembrarUsuario() {
        var usuario = UsuarioJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("20161020123")
                .nombre("Ana Perez")
                .email("ana@uco.edu.co")
                .contacto("573001112233")
                .estadoId("ACTIVO")
                .build();
        entityManager.persistAndFlush(usuario);
        return usuario.getId();
    }

    @Test
    void debeGuardarCoordinador_cuandoSePersiste() {
        // Arrange
        var adapter = new CoordinadorCommandOutputAdapter(coordinadorCommandRepository, logger);
        var usuarioId = sembrarUsuario();

        // Act
        adapter.guardar(new CoordinadorEntity(usuarioId));

        // Assert
        assertThat(coordinadorCommandRepository.existsById(usuarioId)).isTrue();
        verify(logger).debug(AgregarCoordinadorKey.LOG_GUARDADO, usuarioId);
    }

    @Test
    void debeRetornarTrue_cuandoElUsuarioYaEsCoordinador() {
        // Arrange
        var adapter = new CoordinadorCommandOutputAdapter(coordinadorCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(CoordinadorJpaEntity.builder().usuarioId(usuarioId).build());

        // Act
        var existe = adapter.existePorUsuario(usuarioId);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElUsuarioNoEsCoordinador() {
        // Arrange
        var adapter = new CoordinadorCommandOutputAdapter(coordinadorCommandRepository, logger);

        // Act
        var existe = adapter.existePorUsuario(UUID.randomUUID());

        // Assert
        assertThat(existe).isFalse();
    }
}
