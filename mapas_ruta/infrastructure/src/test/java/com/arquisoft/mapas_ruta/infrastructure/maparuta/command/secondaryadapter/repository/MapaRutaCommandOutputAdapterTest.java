package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;
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
