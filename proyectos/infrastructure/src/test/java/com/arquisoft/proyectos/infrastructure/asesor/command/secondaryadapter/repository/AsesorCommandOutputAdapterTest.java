package com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
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

    @Test
    void debeGuardarAsesor_cuandoSePersiste() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        adapter.guardar(new AsesorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, UtilFecha.VACIO));

        // Assert
        assertThat(asesorCommandRepository.existsById(id)).isTrue();
        verify(logger).debug(AsesorKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRetornarEntidadMapeada_cuandoExistePorId() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        entityManager.persistAndFlush(AsesorJpaEntity.builder()
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
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);

        // Act
        var resultado = adapter.obtenerPorId(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeActualizarLosDatosPersistidos_cuandoSeInvocaActualizar() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var id = UUID.randomUUID();
        entityManager.persistAndFlush(AsesorJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(Instant.parse("2026-09-01T10:00:00Z")).build());
        var nuevoOcurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        adapter.actualizar(new AsesorEntity(
                id, "20161020999", "Ana Actualizada", "actualizada@uco.edu.co", nuevoOcurridoEn, UtilFecha.VACIO));

        // Assert
        var resultado = adapter.obtenerPorId(id);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().identificador()).isEqualTo("20161020999");
        assertThat(resultado.get().nombre()).isEqualTo("Ana Actualizada");
        assertThat(resultado.get().ocurridoEn()).isEqualTo(nuevoOcurridoEn);
        verify(logger).debug(AsesorKey.LOG_ACTUALIZADO, id);
    }

    private UUID sembrarAsesor(Instant ocurridoEn, Instant eliminadoEn) {
        var id = UUID.randomUUID();
        entityManager.persistAndFlush(AsesorJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(ocurridoEn).eliminadoEn(eliminadoEn).build());
        return id;
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEn_cuandoSeEliminaLogicamente() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var id = sembrarAsesor(Instant.parse("2026-09-01T10:00:00Z"), null);
        var ocurridoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        adapter.eliminarLogica(id, ocurridoEn);

        // Assert
        var fila = entityManager.find(AsesorJpaEntity.class, id);
        assertThat(fila.getEliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(fila.getOcurridoEn()).isEqualTo(ocurridoEn);
        verify(logger).debug(AsesorKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeLimpiarEliminadoEnYRefrescarDatos_cuandoSeReactiva() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var baja = Instant.parse("2026-09-10T10:00:00Z");
        var id = sembrarAsesor(baja, baja);
        var ocurridoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        adapter.reactivar(new AsesorEntity(
                id, "20161020999", "Ana Gomez", "ana.gomez@uco.edu.co", ocurridoEn, UtilFecha.VACIO));

        // Assert
        var fila = entityManager.find(AsesorJpaEntity.class, id);
        assertThat(fila.getEliminadoEn()).isNull();
        assertThat(fila.getIdentificador()).isEqualTo("20161020999");
        assertThat(fila.getNombre()).isEqualTo("Ana Gomez");
        assertThat(fila.getEmail()).isEqualTo("ana.gomez@uco.edu.co");
        assertThat(fila.getOcurridoEn()).isEqualTo(ocurridoEn);
        verify(logger).debug(AsesorKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void noDebeReactivar_cuandoSeActualizaUnaFilaEliminada() {
        // Arrange
        var adapter = new AsesorCommandOutputAdapter(asesorCommandRepository, logger);
        var baja = Instant.parse("2026-09-10T10:00:00Z");
        var id = sembrarAsesor(baja, baja);
        var ocurridoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        adapter.actualizar(new AsesorEntity(
                id, "20161020999", "Ana Actualizada", "actualizada@uco.edu.co", ocurridoEn, baja));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var fila = entityManager.find(AsesorJpaEntity.class, id);
        assertThat(fila.getEliminadoEn()).isEqualTo(baja);
        assertThat(fila.getNombre()).isEqualTo("Ana Actualizada");
    }
}
