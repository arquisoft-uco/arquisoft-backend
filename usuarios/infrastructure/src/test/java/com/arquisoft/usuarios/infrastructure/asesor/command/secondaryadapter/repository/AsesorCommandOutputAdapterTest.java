package com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorKey;
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
class AsesorCommandOutputAdapterTest {

    @Autowired
    private AsesorCommandRepository asesorCommandRepository;

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
    void debeGuardarAsesor_cuandoSePersiste() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var usuarioId = sembrarUsuario();

        // Act
        adapter.guardar(new AsesorEntity(usuarioId, UtilFecha.VACIO));

        // Assert
        assertThat(asesorCommandRepository.existsById(usuarioId)).isTrue();
        verify(logger).debug(AgregarAsesorKey.LOG_GUARDADO, usuarioId);
    }

    @Test
    void debeRetornarAsesor_cuandoElUsuarioYaEsAsesor() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(AsesorJpaEntity.builder().usuarioId(usuarioId).build());

        // Act
        var asesor = adapter.obtenerPorUsuario(usuarioId);

        // Assert
        assertThat(asesor).isPresent();
        assertThat(asesor.get().usuario()).isEqualTo(usuarioId);
        assertThat(asesor.get().eliminadoEn()).isEqualTo(UtilFecha.VACIO);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoEsAsesor() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);

        // Act
        var asesor = adapter.obtenerPorUsuario(UUID.randomUUID());

        // Assert
        assertThat(asesor).isEmpty();
    }

    @Test
    void debePersistirEliminadoEn_cuandoSeEliminaLogicamente() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(AsesorJpaEntity.builder().usuarioId(usuarioId).build());
        var eliminadoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        adapter.eliminarLogica(usuarioId, eliminadoEn);

        // Assert
        assertThat(entityManager.find(AsesorJpaEntity.class, usuarioId).getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(adapter.obtenerPorUsuario(usuarioId)).map(AsesorEntity::eliminadoEn).contains(eliminadoEn);
        verify(logger).debug(AgregarAsesorKey.LOG_ACTUALIZADO, usuarioId);
    }

    @Test
    void debeDejarEliminadoEnNulo_cuandoSeReactiva() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(AsesorJpaEntity.builder()
                .usuarioId(usuarioId).eliminadoEn(Instant.parse("2026-09-23T10:00:00Z")).build());

        // Act
        adapter.reactivar(usuarioId);

        // Assert
        assertThat(entityManager.find(AsesorJpaEntity.class, usuarioId).getEliminadoEn()).isNull();
        verify(logger).debug(AgregarAsesorKey.LOG_ACTUALIZADO, usuarioId);
    }
}
