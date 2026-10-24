package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AsesorProyectoGradoKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class AsesorProyectoGradoCommandOutputAdapterTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");
    private static final Instant FINALIZADO_EN = Instant.parse("2026-10-21T10:00:00Z");

    @Autowired
    private AsesorProyectoGradoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private AsesorProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorProyectoGradoCommandOutputAdapter(repository, logger);
    }

    @Test
    void debeBuscarPorElParAsesorProyecto_cuandoExisteElVinculo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var buscado = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, OCURRIDO_EN, UtilFecha.VACIO);
        var otroProyecto = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), asesor, UtilUUID.generarNuevoUUID(), OCURRIDO_EN, UtilFecha.VACIO);
        adapter.guardar(buscado);
        adapter.guardar(otroProyecto);
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorAsesorYProyectoGrado(asesor, proyecto);
        var ausente = adapter.obtenerPorAsesorYProyectoGrado(UtilUUID.generarNuevoUUID(), proyecto);

        // Assert
        assertThat(encontrado).contains(buscado);
        assertThat(ausente).isEmpty();
        verify(logger).debug(AsesorProyectoGradoKey.LOG_GUARDADO, buscado.id());
    }

    @Test
    void debeActualizarLaFilaExistente_cuandoSeGuardaElVinculoFinalizado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var vigente = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, OCURRIDO_EN, UtilFecha.VACIO);
        adapter.guardar(vigente);
        entityManager.flush();
        entityManager.clear();
        var finalizado = new AsesorProyectoGradoEntity(vigente.id(), asesor, proyecto, FINALIZADO_EN, FINALIZADO_EN);

        // Act
        adapter.guardar(finalizado);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.obtenerPorAsesorYProyectoGrado(asesor, proyecto)).contains(finalizado);
        assertThat(entityManager.find(AsesorProyectoGradoJpaEntity.class, vigente.id()).getFinalizadoEn())
                .isEqualTo(FINALIZADO_EN);
    }

    @Test
    void debeGuardarFinalizadoEnNulo_cuandoElVinculoEstaVigente() {
        // Arrange
        var vigente = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                OCURRIDO_EN, UtilFecha.VACIO);

        // Act
        adapter.guardar(vigente);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(AsesorProyectoGradoJpaEntity.class, vigente.id()).getFinalizadoEn()).isNull();
    }

    @Test
    void debeRechazarElParRepetido_cuandoSeGuardaElMismoAsesorEnElMismoProyecto() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        adapter.guardar(new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, OCURRIDO_EN, UtilFecha.VACIO));
        entityManager.flush();
        var repetido = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, OCURRIDO_EN, UtilFecha.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> {
            adapter.guardar(repetido);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }
}
