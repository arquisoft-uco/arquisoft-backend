package com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
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
class CoordinadorProyectosCommandOutputAdapterTest {

    @Autowired
    private CoordinadorProyectosCommandRepository coordinadorCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    @Test
    void debeGuardarCoordinador_cuandoSePersiste() {
        // Arrange
        var adapter = new CoordinadorProyectosCommandOutputAdapter(coordinadorCommandRepository, logger);
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        adapter.guardar(new CoordinadorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn));

        // Assert
        assertThat(coordinadorCommandRepository.existsById(id)).isTrue();
        verify(logger).debug(CoordinadorKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRetornarEntidadMapeada_cuandoExistePorId() {
        // Arrange
        var adapter = new CoordinadorProyectosCommandOutputAdapter(coordinadorCommandRepository, logger);
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        entityManager.persistAndFlush(CoordinadorJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(ocurridoEn).build());

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(id);
        assertThat(resultado.get().ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var adapter = new CoordinadorProyectosCommandOutputAdapter(coordinadorCommandRepository, logger);

        // Act
        var resultado = adapter.obtenerPorId(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
