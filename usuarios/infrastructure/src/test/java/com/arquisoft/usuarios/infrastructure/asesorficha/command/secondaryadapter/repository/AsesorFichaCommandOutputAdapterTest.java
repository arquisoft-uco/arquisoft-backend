package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorFichaKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

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
        adapter.guardar(new AsesorFichaEntity(usuarioId));

        // Assert
        assertThat(asesorFichaCommandRepository.existsById(usuarioId)).isTrue();
        verify(logger).debug(AgregarAsesorFichaKey.LOG_GUARDADO, usuarioId);
    }

    @Test
    void debeRetornarTrue_cuandoElUsuarioYaEsAsesorFicha() {
        // Arrange
        var adapter = new AsesorFichaCommandOutputAdapter(asesorFichaCommandRepository, logger);
        var usuarioId = sembrarUsuario();
        adapter.guardar(new AsesorFichaEntity(usuarioId));

        // Act
        var existe = adapter.existePorUsuario(usuarioId);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElUsuarioNoEsAsesorFicha() {
        // Arrange
        var adapter = new AsesorFichaCommandOutputAdapter(asesorFichaCommandRepository, logger);

        // Act
        var existe = adapter.existePorUsuario(UUID.randomUUID());

        // Assert
        assertThat(existe).isFalse();
    }
}
