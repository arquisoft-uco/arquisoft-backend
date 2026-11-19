package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import(ProyectoGradoCommandOutputAdapter.class)
class ProyectoGradoCommandOutputAdapterTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-01T10:00:00Z");

    @MockitoBean
    private AppLogger logger;

    @Autowired
    private ProyectoGradoCommandOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeRetornarLaEntity_cuandoElProyectoExiste() {
        // Arrange
        var proyecto = entityManager.persistFlushFind(ProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estadoProyectoGradoId("EN_PROCESO")
                .coordinadorId(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(UtilUUID.generarNuevoUUID())
                .tituloProyecto("Titulo del proyecto")
                .build());

        // Act
        var resultado = adapter.obtenerPorId(proyecto.getId());

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(proyecto.getId());
            assertThat(entity.estadoProyectoGrado()).isEqualTo("EN_PROCESO");
            assertThat(entity.coordinador()).isEqualTo(proyecto.getCoordinadorId());
            assertThat(entity.fichaPerfil()).isEqualTo(proyecto.getFichaPerfilId());
            assertThat(entity.tituloProyecto()).isEqualTo("Titulo del proyecto");
        });
    }

    @Test
    void debeRetornarVacio_cuandoElProyectoNoExiste() {
        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    private ProyectoGradoJpaEntity persistir(Instant ocurridoEn) {
        return entityManager.persistFlushFind(ProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estadoProyectoGradoId("EN_PROCESO")
                .coordinadorId(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(UtilUUID.generarNuevoUUID())
                .tituloProyecto("Titulo del proyecto")
                .ocurridoEn(ocurridoEn)
                .build());
    }

    @Test
    void debeGuardarYExistir_cuandoSeGuardaUnProyecto() {
        // Arrange
        var entity = new ProyectoGradoEntity(UtilUUID.generarNuevoUUID(), "EN_PROCESO",
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Titulo", OCURRIDO_EN);

        // Act
        adapter.guardar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.existePorId(entity.id())).isTrue();
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
        assertThat(adapter.obtenerPorId(entity.id())).contains(entity);
        verify(logger).debug(any(ClaveMensaje.class), eq(entity.id()));
    }

    @Test
    void debeActualizarEstado_cuandoOcurridoEnEsPosteriorOColumnaNula() {
        // Arrange
        var conFecha = persistir(OCURRIDO_EN);
        var sinFecha = persistir(null);
        var posterior = OCURRIDO_EN.plusSeconds(60);

        // Act
        adapter.actualizarEstado(conFecha.getId(), "ATRASADO", posterior);
        adapter.actualizarEstado(sinFecha.getId(), "ATRASADO", OCURRIDO_EN);
        entityManager.clear();

        // Assert
        var actualizadoConFecha = entityManager.find(ProyectoGradoJpaEntity.class, conFecha.getId());
        var actualizadoSinFecha = entityManager.find(ProyectoGradoJpaEntity.class, sinFecha.getId());
        assertThat(actualizadoConFecha.getEstadoProyectoGradoId()).isEqualTo("ATRASADO");
        assertThat(actualizadoConFecha.getOcurridoEn()).isEqualTo(posterior);
        assertThat(actualizadoSinFecha.getEstadoProyectoGradoId()).isEqualTo("ATRASADO");
        assertThat(actualizadoSinFecha.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void noDebeActualizar_cuandoOcurridoEnEsAnterior() {
        // Arrange
        var proyecto = persistir(OCURRIDO_EN);

        // Act
        adapter.actualizarEstado(proyecto.getId(), "ATRASADO", OCURRIDO_EN.minusSeconds(60));
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(ProyectoGradoJpaEntity.class, proyecto.getId());
        assertThat(guardado.getEstadoProyectoGradoId()).isEqualTo("EN_PROCESO");
        assertThat(guardado.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
    }
}
