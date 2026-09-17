package com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectosKey;
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

    @Test
    void debeGuardarEstudiante_cuandoSePersiste() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        adapter.guardar(new EstudianteEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, UtilFecha.VACIO));

        // Assert
        assertThat(estudianteCommandRepository.existsById(id)).isTrue();
        verify(logger).debug(EstudianteProyectosKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRetornarEntidadMapeada_cuandoExistePorId() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        entityManager.persistAndFlush(EstudianteJpaEntity.builder()
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
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);

        // Act
        var resultado = adapter.obtenerPorId(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    private UUID sembrar(Instant ocurridoEn, Instant eliminadoEn) {
        var id = UUID.randomUUID();
        entityManager.persistAndFlush(EstudianteJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(ocurridoEn).eliminadoEn(eliminadoEn).build());
        return id;
    }

    @Test
    void debeDevolverEliminadosConSuFecha_cuandoObtenerPorIdEncuentraUnaBaja() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var baja = Instant.parse("2026-09-16T10:00:00Z");
        var id = sembrar(baja, baja);

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).map(EstudianteEntity::eliminadoEn).contains(baja);
    }

    @Test
    void debeMarcarEliminadoYActualizarOcurridoEn_cuandoSeEliminaLogicamente() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var id = sembrar(Instant.parse("2026-09-01T10:00:00Z"), null);
        var baja = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        adapter.eliminarLogica(id, baja);

        // Assert
        var guardado = entityManager.find(EstudianteJpaEntity.class, id);
        assertThat(guardado.getEliminadoEn()).isEqualTo(baja);
        assertThat(guardado.getOcurridoEn()).isEqualTo(baja);
        verify(logger).debug(EstudianteProyectosKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeLimpiarEliminadoYRefrescarDatos_cuandoSeReactiva() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var baja = Instant.parse("2026-09-10T10:00:00Z");
        var id = sembrar(baja, baja);
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        adapter.reactivar(new EstudianteEntity(id, "20161020999", "Ana Gomez", "ana.gomez@uco.edu.co", ocurridoEn,
                UtilFecha.VACIO));

        // Assert
        var guardado = entityManager.find(EstudianteJpaEntity.class, id);
        assertThat(guardado.getEliminadoEn()).isNull();
        assertThat(guardado.getIdentificador()).isEqualTo("20161020999");
        assertThat(guardado.getNombre()).isEqualTo("Ana Gomez");
        assertThat(guardado.getEmail()).isEqualTo("ana.gomez@uco.edu.co");
        assertThat(guardado.getOcurridoEn()).isEqualTo(ocurridoEn);
        verify(logger).debug(EstudianteProyectosKey.LOG_ACTUALIZADO, id);
    }
}
