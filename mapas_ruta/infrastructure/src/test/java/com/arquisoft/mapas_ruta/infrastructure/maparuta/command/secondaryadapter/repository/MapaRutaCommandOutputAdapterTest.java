package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import(MapaRutaCommandOutputAdapter.class)
class MapaRutaCommandOutputAdapterTest {

    @Autowired
    private MapaRutaCommandOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @MockitoBean
    private AppLogger logger;

    @Test
    void debePersistirElMapaRuta_cuandoSeRegistra() {
        // Arrange
        var entity = new MapaRutaEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));

        // Act
        adapter.registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(MapaRutaJpaEntity.class, entity.id());
        assertThat(guardado.getProyectoGradoId()).isEqualTo(entity.proyectoGrado());
        assertThat(guardado.getFechaInicio()).isEqualTo(entity.fechaInicio());
        assertThat(guardado.getFechaFin()).isEqualTo(entity.fechaFin());
        verify(logger).debug(any(ClaveMensaje.class), eq(entity.id()));
    }

    @Test
    void debeObtenerPorId() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(MapaRutaJpaEntity.builder()
                .id(id)
                .proyectoGradoId(proyectoGrado)
                .fechaInicio(LocalDate.of(2026, 10, 1))
                .fechaFin(LocalDate.of(2026, 12, 1))
                .build());
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorId(id);
        var ausente = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrado).contains(new MapaRutaEntity(
                id, proyectoGrado, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1)));
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeObtenerElMapa_cuandoConsultaPorTarea() {
        // Arrange
        var mapaId = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var tareaId = UtilUUID.generarNuevoUUID();
        entityManager.persist(MapaRutaJpaEntity.builder()
                .id(mapaId)
                .proyectoGradoId(proyectoGrado)
                .fechaInicio(LocalDate.of(2026, 10, 1))
                .fechaFin(LocalDate.of(2026, 12, 1))
                .build());
        entityManager.persist(MapaRutaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(UtilUUID.generarNuevoUUID())
                .fechaInicio(LocalDate.of(2026, 11, 1))
                .fechaFin(LocalDate.of(2027, 1, 1))
                .build());
        entityManager.persist(TareaJpaEntity.builder()
                .id(tareaId)
                .mapaRutaId(mapaId)
                .descripcion("Redactar capitulo")
                .asesorId(UtilUUID.generarNuevoUUID())
                .estadoItemId("NUEVA")
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorTarea(tareaId);

        // Assert
        assertThat(encontrado).contains(new MapaRutaEntity(
                mapaId, proyectoGrado, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1)));
    }

    @Test
    void debeDevolverVacio_cuandoLaTareaNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerPorTarea(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeActualizarSoloFechaFin_sinTocarFechaInicio() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(MapaRutaJpaEntity.builder()
                .id(id)
                .proyectoGradoId(proyectoGrado)
                .fechaInicio(LocalDate.of(2026, 10, 1))
                .fechaFin(LocalDate.of(2026, 12, 1))
                .build());

        // Act
        adapter.actualizarFechaFin(id, LocalDate.of(2026, 12, 20));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(MapaRutaJpaEntity.class, id);
        assertThat(guardado.getFechaFin()).isEqualTo(LocalDate.of(2026, 12, 20));
        assertThat(guardado.getFechaInicio()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(guardado.getProyectoGradoId()).isEqualTo(proyectoGrado);
        verify(logger).debug(any(ClaveMensaje.class), eq(id));
    }

    @Test
    void debeIndicarExistencia_cuandoConsultaPorProyectoGrado() {
        // Arrange
        var conMapa = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(MapaRutaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(conMapa)
                .fechaInicio(LocalDate.of(2026, 10, 1))
                .fechaFin(LocalDate.of(2026, 12, 1))
                .build());

        // Act & Assert
        assertThat(adapter.existePorProyectoGrado(conMapa)).isTrue();
        assertThat(adapter.existePorProyectoGrado(UtilUUID.generarNuevoUUID())).isFalse();
    }
}
