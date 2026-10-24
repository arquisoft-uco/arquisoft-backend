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
    void debeObtenerPorId_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(TareaJpaEntity.builder().id(id).mapaRutaId(mapaRuta)
                .descripcion("Redactar capitulo").asesorId(asesor).estadoItemId("EN_PROGRESO").build());

        // Act
        var tarea = adapter.obtenerPorId(id);

        // Assert
        assertThat(tarea).hasValue(new TareaEntity(id, mapaRuta, "Redactar capitulo", asesor, "EN_PROGRESO"));
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeActualizarSoloElEstado_cuandoSeCambia() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var otra = UtilUUID.generarNuevoUUID();
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        entityManager.persist(TareaJpaEntity.builder().id(id).mapaRutaId(mapaRuta)
                .descripcion("Redactar capitulo").asesorId(asesor).estadoItemId("NUEVA").build());
        entityManager.persist(TareaJpaEntity.builder().id(otra).mapaRutaId(mapaRuta)
                .descripcion("Otra tarea").asesorId(asesor).estadoItemId("NUEVA").build());
        entityManager.flush();

        // Act
        adapter.actualizarEstadoItem(id, "FINALIZADA");
        entityManager.clear();

        // Assert
        var actualizada = entityManager.find(TareaJpaEntity.class, id);
        assertThat(actualizada.getEstadoItemId()).isEqualTo("FINALIZADA");
        assertThat(actualizada.getDescripcion()).isEqualTo("Redactar capitulo");
        assertThat(actualizada.getMapaRutaId()).isEqualTo(mapaRuta);
        assertThat(actualizada.getAsesorId()).isEqualTo(asesor);
        assertThat(entityManager.find(TareaJpaEntity.class, otra).getEstadoItemId()).isEqualTo("NUEVA");
        verify(logger).debug(any(ClaveMensaje.class), eq(id));
    }

    @Test
    void debeActualizarDescripcionYAsesor_cuandoTareaExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var otra = UtilUUID.generarNuevoUUID();
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var nuevoAsesor = UtilUUID.generarNuevoUUID();
        entityManager.persist(TareaJpaEntity.builder().id(id).mapaRutaId(mapaRuta)
                .descripcion("Anterior").asesorId(UtilUUID.generarNuevoUUID()).estadoItemId("EN_PROGRESO").build());
        entityManager.persist(TareaJpaEntity.builder().id(otra).mapaRutaId(mapaRuta)
                .descripcion("Otra tarea").asesorId(UtilUUID.generarNuevoUUID()).estadoItemId("NUEVA").build());
        entityManager.flush();

        // Act
        adapter.actualizarInformacion(id, "Redactar capitulo", nuevoAsesor);
        entityManager.clear();

        // Assert
        var actualizada = entityManager.find(TareaJpaEntity.class, id);
        assertThat(actualizada.getDescripcion()).isEqualTo("Redactar capitulo");
        assertThat(actualizada.getAsesorId()).isEqualTo(nuevoAsesor);
        assertThat(actualizada.getMapaRutaId()).isEqualTo(mapaRuta);
        assertThat(actualizada.getEstadoItemId()).isEqualTo("EN_PROGRESO");
        assertThat(entityManager.find(TareaJpaEntity.class, otra).getDescripcion()).isEqualTo("Otra tarea");
        verify(logger).debug(any(ClaveMensaje.class), eq(id));
    }

    @Test
    void debeDetectarOtraTarea_soloSiOtraDelMismoMapaTieneLaDescripcion() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var propia = UtilUUID.generarNuevoUUID();
        entityManager.persist(TareaJpaEntity.builder().id(propia).mapaRutaId(mapaRuta)
                .descripcion("Redactar capitulo").asesorId(UtilUUID.generarNuevoUUID()).estadoItemId("NUEVA").build());
        sembrar(mapaRuta, "Otra tarea");
        sembrar(UtilUUID.generarNuevoUUID(), "Descripcion de otro mapa");
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existeOtraPorMapaRutaYDescripcion(mapaRuta, "Otra tarea", propia)).isTrue();
        assertThat(adapter.existeOtraPorMapaRutaYDescripcion(mapaRuta, "Redactar capitulo", propia)).isFalse();
        assertThat(adapter.existeOtraPorMapaRutaYDescripcion(mapaRuta, "Descripcion de otro mapa", propia))
                .isFalse();
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
