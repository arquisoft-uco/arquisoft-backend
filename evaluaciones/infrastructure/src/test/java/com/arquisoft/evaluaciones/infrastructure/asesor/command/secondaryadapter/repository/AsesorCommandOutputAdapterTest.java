package com.arquisoft.evaluaciones.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.AsesorKey;
import com.arquisoft.shared.util.UtilFecha;
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
class AsesorCommandOutputAdapterTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-11T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AsesorCommandRepository repository;

    private final AppLogger logger = mock(AppLogger.class);

    private AsesorCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorCommandOutputAdapter(repository, logger);
    }

    @Test
    void debeGuardarVigenteYLeerloDeVuelta_cuandoSePersiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var asesor = new AsesorEntity(id, "ID-1", "Asesor Uno", "j1@uco.edu.co", OCURRIDO_EN, UtilFecha.VACIO);

        // Act
        adapter.guardar(asesor);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(AsesorJpaEntity.class, id).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorId(id)).contains(asesor);
        verify(logger).debug(AsesorKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRefrescarDatosYLimpiarLaBajaSoloDeEseId_cuandoSeReactiva() {
        // Arrange
        var id = sembrarAsesor(Instant.parse("2026-09-24T10:00:00Z"));
        var otro = sembrarAsesor(Instant.parse("2026-09-24T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();
        var reactivado = new AsesorEntity(id, "ID-N", "Nombre Nuevo", "nuevo@uco.edu.co", OCURRIDO_EN,
                UtilFecha.VACIO);

        // Act
        adapter.reactivar(reactivado);

        // Assert
        assertThat(adapter.obtenerPorId(id)).contains(reactivado);
        assertThat(adapter.obtenerPorId(otro)).map(AsesorEntity::eliminadoEn)
                .contains(Instant.parse("2026-09-24T10:00:00Z"));
        verify(logger).debug(AsesorKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEnSoloDeEseAsesor_cuandoSeEliminaLogica() {
        // Arrange
        var id = sembrarAsesor(null);
        var otro = sembrarAsesor(null);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.eliminarLogica(id, OCURRIDO_EN);

        // Assert
        var removido = entityManager.find(AsesorJpaEntity.class, id);
        assertThat(removido.getEliminadoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(removido.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(entityManager.find(AsesorJpaEntity.class, otro).getEliminadoEn()).isNull();
        verify(logger).debug(AsesorKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Act
        var asesor = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(asesor).isEmpty();
    }

    private UUID sembrarAsesor(Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(AsesorJpaEntity.builder()
                .id(id)
                .identificador("ID-" + id.toString().substring(0, 8))
                .nombre("Asesor")
                .email("asesor@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z"))
                .eliminadoEn(eliminadoEn)
                .build());
        return id;
    }
}
