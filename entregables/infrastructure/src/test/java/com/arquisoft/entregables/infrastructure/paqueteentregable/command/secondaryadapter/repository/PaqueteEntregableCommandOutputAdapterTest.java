package com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.entity.PaqueteEntregableJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class PaqueteEntregableCommandOutputAdapterTest {

    @Autowired
    private PaqueteEntregableCommandRepository paqueteEntregableCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private PaqueteEntregableCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PaqueteEntregableCommandOutputAdapter(paqueteEntregableCommandRepository, logger);
    }

    @Test
    void debeGuardarYRecuperar() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var generadoEn = Instant.parse("2026-11-08T10:00:00Z");

        // Act
        adapter.guardar(new PaqueteEntregableEntity(id, entregable, "bucket", "paquetes/x.zip", "p.zip", 99L, generadoEn));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(PaqueteEntregableJpaEntity.class, id);
        assertThat(guardado.getEntregableProyectoGradoId()).isEqualTo(entregable);
        assertThat(guardado.getBucket()).isEqualTo("bucket");
        assertThat(guardado.getClaveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(guardado.getNombreArchivo()).isEqualTo("p.zip");
        assertThat(guardado.getTamanoBytes()).isEqualTo(99L);
        assertThat(guardado.getGeneradoEn()).isEqualTo(generadoEn);
        verify(logger).debug(PaqueteEntregableKey.LOG_GUARDADO, id);
    }

    @Test
    void debeObtenerLosPaquetesDelProyecto_cuandoElEntregablePerteneceAlProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var delProyecto = UtilUUID.generarNuevoUUID();
        var deOtroProyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(EntregableProyectoGradoJpaEntity.builder()
                .id(delProyecto).proyectoGrado(proyecto).version(1).asesor(UtilUUID.generarNuevoUUID()).build());
        entityManager.persist(EntregableProyectoGradoJpaEntity.builder()
                .id(deOtroProyecto).proyectoGrado(UtilUUID.generarNuevoUUID()).version(1)
                .asesor(UtilUUID.generarNuevoUUID()).build());
        persistirPaquete(delProyecto);
        persistirPaquete(deOtroProyecto);
        entityManager.flush();
        entityManager.clear();

        // Act
        var paquetes = adapter.obtenerPorProyectoGrado(proyecto);

        // Assert
        assertThat(paquetes).singleElement().satisfies(paquete -> {
            assertThat(paquete.entregableProyectoGrado()).isEqualTo(delProyecto);
            assertThat(paquete.bucket()).isEqualTo("bucket");
            assertThat(paquete.claveObjeto()).isEqualTo("paquetes/x.zip");
        });
    }

    private void persistirPaquete(java.util.UUID entregable) {
        entityManager.persist(PaqueteEntregableJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).entregableProyectoGradoId(entregable).bucket("bucket")
                .claveObjeto("paquetes/x.zip").nombreArchivo("p.zip").tamanoBytes(1L)
                .generadoEn(Instant.now()).build());
    }
}
