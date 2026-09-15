package com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

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
        adapter.guardar(new AsesorEntity(usuarioId));

        // Assert
        assertThat(asesorCommandRepository.existsById(usuarioId)).isTrue();
        verify(logger).debug(AgregarAsesorKey.LOG_GUARDADO, usuarioId);
    }

    @Test
    void debeRetornarTrue_cuandoElUsuarioYaEsAsesor() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        entityManager.persistAndFlush(AsesorJpaEntity.builder().usuarioId(usuarioId).build());

        // Act
        var existe = adapter.existePorUsuario(usuarioId);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElUsuarioNoEsAsesor() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);

        // Act
        var existe = adapter.existePorUsuario(UUID.randomUUID());

        // Assert
        assertThat(existe).isFalse();
    }
}
