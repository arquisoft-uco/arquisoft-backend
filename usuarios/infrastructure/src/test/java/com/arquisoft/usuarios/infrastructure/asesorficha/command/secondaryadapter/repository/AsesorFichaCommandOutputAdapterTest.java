package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorFichaKey;
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
class AsesorFichaCommandOutputAdapterTest {

    @Autowired
    private AsesorFichaCommandRepository asesorFichaCommandRepository;

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
    void debeGuardarAsesorFicha_cuandoSePersiste() {
        // Arrange
        var adapter = new AsesorFichaCommandOutputAdapter(asesorFichaCommandRepository, logger);
        var usuarioId = sembrarUsuario();

        // Act
        adapter.guardar(new AsesorFichaEntity(usuarioId, UtilFecha.VACIO));

        // Assert
        assertThat(asesorFichaCommandRepository.existsById(usuarioId)).isTrue();
        verify(logger).debug(AgregarAsesorFichaKey.LOG_GUARDADO, usuarioId);
    }

    @Test
    void debeRetornarElAsesorFicha_cuandoElUsuarioYaEsAsesorFicha() {
        // Arrange
        var adapter = new AsesorFichaCommandOutputAdapter(asesorFichaCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        adapter.guardar(new AsesorFichaEntity(usuarioId, UtilFecha.VACIO));

        // Act
        var asesorFicha = adapter.obtenerPorUsuario(usuarioId);

        // Assert
        assertThat(asesorFicha).isPresent();
        assertThat(asesorFicha.get().eliminadoEn()).isEqualTo(UtilFecha.VACIO);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoEsAsesorFicha() {
        // Arrange
        var adapter = new AsesorFichaCommandOutputAdapter(asesorFichaCommandRepository, logger);

        // Act
        var asesorFicha = adapter.obtenerPorUsuario(UUID.randomUUID());

        // Assert
        assertThat(asesorFicha).isEmpty();
    }

    @Test
    void debePersistirEliminadoEn_cuandoSeEliminaLogicamente() {
        // Arrange
        var adapter = new AsesorFichaCommandOutputAdapter(asesorFichaCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(AsesorFichaJpaEntity.builder().usuarioId(usuarioId).build());
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        adapter.eliminarLogica(usuarioId, eliminadoEn);

        // Assert
        assertThat(entityManager.find(AsesorFichaJpaEntity.class, usuarioId).getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(adapter.obtenerPorUsuario(usuarioId)).map(AsesorFichaEntity::eliminadoEn).contains(eliminadoEn);
        verify(logger).debug(AgregarAsesorFichaKey.LOG_ACTUALIZADO, usuarioId);
    }

    @Test
    void debeDejarEliminadoEnNulo_cuandoSeReactiva() {
        // Arrange
        var adapter = new AsesorFichaCommandOutputAdapter(asesorFichaCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(AsesorFichaJpaEntity.builder()
                .usuarioId(usuarioId).eliminadoEn(Instant.parse("2026-09-24T10:00:00Z")).build());

        // Act
        adapter.reactivar(usuarioId);

        // Assert
        assertThat(entityManager.find(AsesorFichaJpaEntity.class, usuarioId).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuarioId)).map(AsesorFichaEntity::eliminadoEn)
                .contains(UtilFecha.VACIO);
        verify(logger).debug(AgregarAsesorFichaKey.LOG_ACTUALIZADO, usuarioId);
    }
}
