package com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EstudianteCommandOutputAdapterTest {

    @Autowired
    private EstudianteCommandRepository estudianteCommandRepository;

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
    void debeGuardarEstudiante_cuandoSePersiste() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var usuarioId = sembrarUsuario();

        // Act
        adapter.guardar(new EstudianteEntity(usuarioId));

        // Assert
        assertThat(estudianteCommandRepository.existsById(usuarioId)).isTrue();
        verify(logger).debug(com.arquisoft.shared.message.key.usuarios.AgregarEstudianteKey.LOG_GUARDADO,
                usuarioId);
    }

    @Test
    void debeRetornarTrue_cuandoElUsuarioYaEsEstudiante() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(EstudianteJpaEntity.builder().usuarioId(usuarioId).build());

        // Act
        var existe = adapter.existePorUsuario(usuarioId);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElUsuarioNoEsEstudiante() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);

        // Act
        var existe = adapter.existePorUsuario(UUID.randomUUID());

        // Assert
        assertThat(existe).isFalse();
    }
}
