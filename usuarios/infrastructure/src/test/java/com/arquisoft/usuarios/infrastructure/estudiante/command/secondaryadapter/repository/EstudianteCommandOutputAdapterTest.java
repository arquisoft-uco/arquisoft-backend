package com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarEstudianteKey;
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
        adapter.guardar(new EstudianteEntity(usuarioId, UtilFecha.VACIO));

        // Assert
        assertThat(estudianteCommandRepository.existsById(usuarioId)).isTrue();
        verify(logger).debug(com.arquisoft.shared.message.key.usuarios.AgregarEstudianteKey.LOG_GUARDADO,
                usuarioId);
    }

    @Test
    void debeRetornarEstudiante_cuandoElUsuarioYaEsEstudiante() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(EstudianteJpaEntity.builder().usuarioId(usuarioId).build());

        // Act
        var estudiante = adapter.obtenerPorUsuario(usuarioId);

        // Assert
        assertThat(estudiante).map(EstudianteEntity::usuario).contains(usuarioId);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoEsEstudiante() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);

        // Act
        var estudiante = adapter.obtenerPorUsuario(UUID.randomUUID());

        // Assert
        assertThat(estudiante).isEmpty();
    }

    @Test
    void debeTraducirColumnaNulaAVacio_cuandoElEstudianteEstaVigente() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(EstudianteJpaEntity.builder().usuarioId(usuarioId).build());

        // Act
        var estudiante = adapter.obtenerPorUsuario(usuarioId);

        // Assert
        assertThat(estudiante).map(EstudianteEntity::eliminadoEn).contains(UtilFecha.VACIO);
    }

    @Test
    void debePersistirEliminadoEn_cuandoSeEliminaLogicamente() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(EstudianteJpaEntity.builder().usuarioId(usuarioId).build());
        var eliminadoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        adapter.eliminarLogica(usuarioId, eliminadoEn);

        // Assert
        assertThat(entityManager.find(EstudianteJpaEntity.class, usuarioId).getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(adapter.obtenerPorUsuario(usuarioId)).map(EstudianteEntity::eliminadoEn).contains(eliminadoEn);
        verify(logger).debug(AgregarEstudianteKey.LOG_ACTUALIZADO, usuarioId);
    }

    @Test
    void debeDejarEliminadoEnNulo_cuandoSeReactiva() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(EstudianteJpaEntity.builder()
                .usuarioId(usuarioId).eliminadoEn(Instant.parse("2026-09-16T10:00:00Z")).build());

        // Act
        adapter.reactivar(usuarioId);

        // Assert
        assertThat(entityManager.find(EstudianteJpaEntity.class, usuarioId).getEliminadoEn()).isNull();
        verify(logger).debug(AgregarEstudianteKey.LOG_ACTUALIZADO, usuarioId);
    }
}
