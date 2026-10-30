package com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteKey;
import com.arquisoft.shared.util.UtilFecha;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
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
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();

        // Act
        adapter.guardar(new EstudianteEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, null));

        // Assert
        assertThat(estudianteCommandRepository.existsById(id)).isTrue();
        verify(logger).debug(EstudianteKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRetornarEntidadMapeada_cuandoExistePorId() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var id = UtilUUID.generarNuevoUUID();
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
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeActualizarLosDatosPersistidos_cuandoSeInvocaActualizar() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(EstudianteJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(Instant.parse("2026-09-01T10:00:00Z")).build());
        var nuevoOcurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        adapter.actualizar(new EstudianteEntity(
                id, "20161020999", "Ana Actualizada", "actualizada@uco.edu.co", nuevoOcurridoEn, null));

        // Assert
        var resultado = adapter.obtenerPorId(id);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().identificador()).isEqualTo("20161020999");
        assertThat(resultado.get().nombre()).isEqualTo("Ana Actualizada");
        assertThat(resultado.get().ocurridoEn()).isEqualTo(nuevoOcurridoEn);
        verify(logger).debug(EstudianteKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEn_cuandoSeEliminaLogicamente() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(EstudianteJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(Instant.parse("2026-09-01T10:00:00Z")).build());
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        adapter.eliminarLogica(id, ocurridoEn);

        // Assert
        var persistido = entityManager.find(EstudianteJpaEntity.class, id);
        assertThat(persistido.getEliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(persistido.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(adapter.obtenerPorId(id)).map(EstudianteEntity::eliminadoEn).contains(ocurridoEn);
        verify(logger).debug(EstudianteKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeActualizarDatosYLimpiarEliminadoEn_cuandoSeReactiva() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var id = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-01T10:00:00Z");
        entityManager.persistAndFlush(EstudianteJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(eliminadoEn).eliminadoEn(eliminadoEn).build());
        var nuevoOcurridoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        adapter.reactivar(new EstudianteEntity(
                id, "20161020999", "Ana Reactivada", "reactivada@uco.edu.co", nuevoOcurridoEn, UtilFecha.VACIO));

        // Assert
        var persistido = entityManager.find(EstudianteJpaEntity.class, id);
        assertThat(persistido.getEliminadoEn()).isNull();
        assertThat(persistido.getIdentificador()).isEqualTo("20161020999");
        assertThat(persistido.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(persistido.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(persistido.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        verify(logger).debug(EstudianteKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeDevolverVigentesYDadosDeBajaSoloDeLosIdsPedidos_cuandoHayOtrosEstudiantes() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var vigente = UtilUUID.generarNuevoUUID();
        var eliminado = UtilUUID.generarNuevoUUID();
        var ajeno = UtilUUID.generarNuevoUUID();
        var baja = Instant.parse("2026-01-01T00:00:00Z");
        sembrar(vigente, "Laura", null);
        sembrar(eliminado, "Pedro", baja);
        sembrar(ajeno, "Ajeno", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorIds(List.of(vigente, eliminado));

        // Assert
        assertThat(resultado).hasSize(2);
        var deLaura = resultado.stream().filter(e -> e.id().equals(vigente)).findFirst().orElseThrow();
        var dePedro = resultado.stream().filter(e -> e.id().equals(eliminado)).findFirst().orElseThrow();
        assertThat(deLaura.nombre()).isEqualTo("Laura");
        assertThat(deLaura.email()).isEqualTo("Laura@uco.edu.co");
        assertThat(deLaura.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(dePedro.eliminadoEn()).isEqualTo(baja);
    }

    @Test
    void debeDevolverSoloLosVinculadosVigentesDelArtefacto_cuandoHayBajasYOtrosArtefactos() {
        // Arrange
        var adapter = new EstudianteCommandOutputAdapter(estudianteCommandRepository, logger);
        var artefacto = UtilUUID.generarNuevoUUID();
        var otroArtefacto = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var segundoVigente = UtilUUID.generarNuevoUUID();
        var eliminado = UtilUUID.generarNuevoUUID();
        var deOtroArtefacto = UtilUUID.generarNuevoUUID();
        sembrar(vigente, "Laura", null);
        sembrar(segundoVigente, "Sara", null);
        sembrar(eliminado, "Pedro", Instant.now());
        sembrar(deOtroArtefacto, "Ajeno", null);
        vincular(artefacto, vigente);
        vincular(artefacto, segundoVigente);
        vincular(artefacto, eliminado);
        vincular(otroArtefacto, deOtroArtefacto);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerVigentesVinculadosAlArtefacto(artefacto);

        // Assert
        assertThat(resultado).extracting(EstudianteEntity::id).containsExactlyInAnyOrder(vigente, segundoVigente);
        assertThat(adapter.obtenerVigentesVinculadosAlArtefacto(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    private void vincular(UUID artefacto, UUID estudiante) {
        entityManager.persist(EstudianteArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).artefacto(artefacto).estudiante(estudiante).build());
    }

    private void sembrar(UUID id, String nombre, Instant eliminadoEn) {
        entityManager.persist(EstudianteJpaEntity.builder()
                .id(id)
                .identificador("ID-" + nombre)
                .nombre(nombre)
                .email(nombre + "@uco.edu.co")
                .ocurridoEn(Instant.EPOCH)
                .eliminadoEn(eliminadoEn)
                .build());
    }
}
