package com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.entity.TareaJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import(TareaCommandOutputAdapter.class)
class TareaCommandOutputAdapterTest {

    @Autowired
    private TareaCommandOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @MockitoBean
    private AppLogger logger;

    private void sembrar(UUID mapaRuta, String descripcion) {
        entityManager.persistAndFlush(TareaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .mapaRutaId(mapaRuta)
                .descripcion(descripcion)
                .asesorId(UtilUUID.generarNuevoUUID())
                .estadoItemId("NUEVA")
                .build());
    }

    @Test
    void debePersistirLaTarea_cuandoSeRegistra() {
        // Arrange
        var entity = new TareaEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), "NUEVA");

        // Act
        adapter.registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardada = entityManager.find(TareaJpaEntity.class, entity.id());
        assertThat(guardada.getMapaRutaId()).isEqualTo(entity.mapaRuta());
        assertThat(guardada.getDescripcion()).isEqualTo("Redactar capitulo");
        assertThat(guardada.getAsesorId()).isEqualTo(entity.asesor());
        assertThat(guardada.getEstadoItemId()).isEqualTo("NUEVA");
        verify(logger).debug(any(ClaveMensaje.class), eq(entity.id()));
    }

    @Test
    void debeIndicarExistencia_soloParaLaMismaDescripcionEnElMismoMapa() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        sembrar(mapaRuta, "Redactar capitulo");

        // Act & Assert
        assertThat(adapter.existePorMapaRutaYDescripcion(mapaRuta, "Redactar capitulo")).isTrue();
        assertThat(adapter.existePorMapaRutaYDescripcion(mapaRuta, "Otra tarea")).isFalse();
        assertThat(adapter.existePorMapaRutaYDescripcion(UtilUUID.generarNuevoUUID(), "Redactar capitulo"))
                .isFalse();
    }
}
