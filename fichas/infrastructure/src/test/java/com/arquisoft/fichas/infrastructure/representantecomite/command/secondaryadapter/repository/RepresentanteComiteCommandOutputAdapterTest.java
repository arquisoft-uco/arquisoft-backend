package com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
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
class RepresentanteComiteCommandOutputAdapterTest {

    private static final Instant ANTES = Instant.parse("2026-09-10T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-09-24T10:00:00Z");

    @Autowired
    private RepresentanteComiteCommandRepository representanteComiteCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private RepresentanteComiteCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RepresentanteComiteCommandOutputAdapter(representanteComiteCommandRepository, logger);
    }

    private UUID sembrar(Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(RepresentanteComiteJpaEntity.builder()
                .id(id)
                .identificador("20161020123")
                .nombre("Ana Perez")
                .email("ana@uco.edu.co")
                .ocurridoEn(ANTES)
                .eliminadoEn(eliminadoEn)
                .build());
        entityManager.clear();
        return id;
    }

    private RepresentanteComiteJpaEntity leer(UUID id) {
        entityManager.flush();
        entityManager.clear();
        return entityManager.find(RepresentanteComiteJpaEntity.class, id);
    }

    @Test
    void debeResponderSoloPorLosVigentes_cuandoSeConsultaExistenciaVigente() {
        // Arrange
        var vigente = sembrar(null);
        var eliminado = sembrar(ANTES);

        // Act
        var existeVigente = adapter.existeVigentePorId(vigente);
        var existeEliminado = adapter.existeVigentePorId(eliminado);
        var existeAusente = adapter.existeVigentePorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(existeVigente).isTrue();
        assertThat(existeEliminado).isFalse();
        assertThat(existeAusente).isFalse();
    }

    @Test
    void debeGuardarYLeerDeVueltaVigente_cuandoSePersisteLaReplica() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(new RepresentanteComiteEntity(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", DESPUES, UtilFecha.VACIO));

        // Assert
        assertThat(leer(id).getEliminadoEn()).isNull();
        var leido = adapter.obtenerPorId(id);
        assertThat(leido).isPresent();
        assertThat(leido.get().identificador()).isEqualTo("20161020123");
        assertThat(leido.get().nombre()).isEqualTo("Ana Perez");
        assertThat(leido.get().email()).isEqualTo("ana@uco.edu.co");
        assertThat(leido.get().ocurridoEn()).isEqualTo(DESPUES);
        assertThat(leido.get().eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(logger).debug(RepresentanteComiteKey.LOG_GUARDADO, id);
    }

    @Test
    void debeIncluirLasBajasConSuFecha_cuandoSeObtienePorId() {
        // Arrange
        var eliminado = sembrar(ANTES);

        // Act
        var leido = adapter.obtenerPorId(eliminado);
        var ausente = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(leido).map(RepresentanteComiteEntity::eliminadoEn).contains(ANTES);
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeActualizarDatosYConservarLaBaja_cuandoSeInvocaActualizar() {
        // Arrange
        var id = sembrar(ANTES);

        // Act
        adapter.actualizar(new RepresentanteComiteEntity(
                id, "20161020999", "Ana Actualizada", "actualizada@uco.edu.co", DESPUES, ANTES));

        // Assert
        var guardado = leer(id);
        assertThat(guardado.getIdentificador()).isEqualTo("20161020999");
        assertThat(guardado.getNombre()).isEqualTo("Ana Actualizada");
        assertThat(guardado.getEmail()).isEqualTo("actualizada@uco.edu.co");
        assertThat(guardado.getOcurridoEn()).isEqualTo(DESPUES);
        assertThat(guardado.getEliminadoEn()).isEqualTo(ANTES);
        verify(logger).debug(RepresentanteComiteKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeMarcarBajaYVersionSinTocarDatos_cuandoSeEliminaLogicamente() {
        // Arrange
        var id = sembrar(null);
        var otro = sembrar(null);

        // Act
        adapter.eliminarLogica(id, DESPUES);

        // Assert
        var guardado = leer(id);
        assertThat(guardado.getEliminadoEn()).isEqualTo(DESPUES);
        assertThat(guardado.getOcurridoEn()).isEqualTo(DESPUES);
        assertThat(guardado.getIdentificador()).isEqualTo("20161020123");
        assertThat(adapter.existeVigentePorId(id)).isFalse();
        assertThat(adapter.existeVigentePorId(otro)).isTrue();
        verify(logger).debug(RepresentanteComiteKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeLimpiarLaBajaYRefrescarDatos_cuandoSeReactiva() {
        // Arrange
        var id = sembrar(ANTES);

        // Act
        adapter.reactivar(new RepresentanteComiteEntity(
                id, "20161020999", "Ana Reactivada", "reactivada@uco.edu.co", DESPUES, UtilFecha.VACIO));

        // Assert
        var guardado = leer(id);
        assertThat(guardado.getEliminadoEn()).isNull();
        assertThat(guardado.getIdentificador()).isEqualTo("20161020999");
        assertThat(guardado.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(guardado.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(guardado.getOcurridoEn()).isEqualTo(DESPUES);
        assertThat(adapter.existeVigentePorId(id)).isTrue();
        verify(logger).debug(RepresentanteComiteKey.LOG_ACTUALIZADO, id);
    }
}
