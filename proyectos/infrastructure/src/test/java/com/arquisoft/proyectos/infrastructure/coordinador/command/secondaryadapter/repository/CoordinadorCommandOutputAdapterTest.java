package com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
import com.arquisoft.shared.util.UtilFecha;
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
class CoordinadorCommandOutputAdapterTest {

    @Autowired
    private CoordinadorCommandRepository coordinadorCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    @Test
    void debeGuardarCoordinador_cuandoSePersiste() {
        // Arrange
        var adapter = new CoordinadorCommandOutputAdapter(coordinadorCommandRepository, logger);
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        adapter.guardar(new CoordinadorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, null));

        // Assert
        assertThat(coordinadorCommandRepository.existsById(id)).isTrue();
        verify(logger).debug(CoordinadorKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRetornarEntidadMapeada_cuandoExistePorId() {
        // Arrange
        var adapter = new CoordinadorCommandOutputAdapter(coordinadorCommandRepository, logger);
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
        var adapter = new CoordinadorCommandOutputAdapter(coordinadorCommandRepository, logger);

        // Act
        var resultado = adapter.obtenerPorId(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeActualizarLosDatosPersistidos_cuandoSeInvocaActualizar() {
        // Arrange
        var adapter = new CoordinadorCommandOutputAdapter(coordinadorCommandRepository, logger);
        var id = UUID.randomUUID();
        entityManager.persistAndFlush(CoordinadorJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(Instant.parse("2026-09-01T10:00:00Z")).build());
        var nuevoOcurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        adapter.actualizar(new CoordinadorEntity(
                id, "20161020999", "Ana Actualizada", "actualizada@uco.edu.co", nuevoOcurridoEn, null));

        // Assert
        var resultado = adapter.obtenerPorId(id);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().identificador()).isEqualTo("20161020999");
        assertThat(resultado.get().nombre()).isEqualTo("Ana Actualizada");
        assertThat(resultado.get().ocurridoEn()).isEqualTo(nuevoOcurridoEn);
        verify(logger).debug(CoordinadorKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEn_cuandoSeEliminaLogicamente() {
        // Arrange
        var adapter = new CoordinadorCommandOutputAdapter(coordinadorCommandRepository, logger);
        var id = UUID.randomUUID();
        entityManager.persistAndFlush(CoordinadorJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(Instant.parse("2026-09-01T10:00:00Z")).build());
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        adapter.eliminarLogica(id, ocurridoEn);

        // Assert
        var persistido = entityManager.find(CoordinadorJpaEntity.class, id);
        assertThat(persistido.getEliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(persistido.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(adapter.obtenerPorId(id)).map(CoordinadorEntity::eliminadoEn).contains(ocurridoEn);
        verify(logger).debug(CoordinadorKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeActualizarDatosYLimpiarEliminadoEn_cuandoSeReactiva() {
        // Arrange
        var adapter = new CoordinadorCommandOutputAdapter(coordinadorCommandRepository, logger);
        var id = UUID.randomUUID();
        var eliminadoEn = Instant.parse("2026-09-01T10:00:00Z");
        entityManager.persistAndFlush(CoordinadorJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(eliminadoEn).eliminadoEn(eliminadoEn).build());
        var nuevoOcurridoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        adapter.reactivar(new CoordinadorEntity(
                id, "20161020999", "Ana Reactivada", "reactivada@uco.edu.co", nuevoOcurridoEn, UtilFecha.VACIO));

        // Assert
        var persistido = entityManager.find(CoordinadorJpaEntity.class, id);
        assertThat(persistido.getEliminadoEn()).isNull();
        assertThat(persistido.getIdentificador()).isEqualTo("20161020999");
        assertThat(persistido.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(persistido.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(persistido.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        verify(logger).debug(CoordinadorKey.LOG_ACTUALIZADO, id);
    }
}
