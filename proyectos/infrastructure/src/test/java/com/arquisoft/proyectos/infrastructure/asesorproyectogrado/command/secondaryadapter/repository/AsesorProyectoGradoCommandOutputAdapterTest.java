package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class AsesorProyectoGradoCommandOutputAdapterTest {

    private static final String ASIGNADO = "ASIGNADO";
    private static final String FINALIZADO = "FINALIZADO";

    @Autowired
    private AsesorProyectoGradoCommandRepository asesorProyectoGradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private static AsesorProyectoGradoEntity vinculo(UUID asesor, UUID proyectoGrado, String estado) {
        return new AsesorProyectoGradoEntity(UtilUUID.generarNuevoUUID(), asesor, proyectoGrado, estado,
                UtilFecha.generarInstanteActual().truncatedTo(ChronoUnit.MILLIS));
    }

    private AsesorProyectoGradoCommandOutputAdapter adapter() {
        return new AsesorProyectoGradoCommandOutputAdapter(asesorProyectoGradoCommandRepository, logger);
    }

    @Test
    void debePersistirElVinculoConTodosSusCampos_cuandoSeAsigna() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vinculo = vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, ASIGNADO);

        // Act
        adapter.asignar(vinculo);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(AsesorProyectoGradoJpaEntity.class, vinculo.id());
        assertThat(guardado.getAsesorId()).isEqualTo(vinculo.asesor());
        assertThat(guardado.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(guardado.getEstadoAsesorId()).isEqualTo(ASIGNADO);
        assertThat(guardado.getFechaAsignado()).isEqualTo(vinculo.fechaAsignado());
        verify(logger).debug(AsesorProyectoGradoKey.LOG_GUARDADO, vinculo.id(), proyectoGrado);
    }

    @Test
    void debeContarSoloLosAsignadosDelProyecto_cuandoHayFinalizadosYDeOtroProyecto() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        adapter.asignar(vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, ASIGNADO));
        adapter.asignar(vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, ASIGNADO));
        adapter.asignar(vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, FINALIZADO));
        adapter.asignar(vinculo(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ASIGNADO));
        entityManager.flush();
        entityManager.clear();

        // Act
        var asignados = adapter.contarPorProyectoGradoYEstado(proyectoGrado, ASIGNADO);

        // Assert
        assertThat(asignados).isEqualTo(2);
    }

    @Test
    void debeObtenerElVinculo_cuandoIdYProyectoCoinciden() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vinculo = vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, ASIGNADO);
        adapter.asignar(vinculo);
        entityManager.flush();
        entityManager.clear();

        // Act
        var obtenido = adapter.obtenerPorIdYProyectoGrado(vinculo.id(), proyectoGrado);

        // Assert
        assertThat(obtenido).contains(vinculo);
    }

    @Test
    void debeRetornarVacio_cuandoElVinculoEsDeOtroProyecto() {
        // Arrange
        var adapter = adapter();
        var vinculo = vinculo(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ASIGNADO);
        adapter.asignar(vinculo);
        entityManager.flush();
        entityManager.clear();

        // Act
        var obtenido = adapter.obtenerPorIdYProyectoGrado(vinculo.id(), UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(obtenido).isEmpty();
    }

    @Test
    void debeActualizarSoloElEstadoDelVinculo_cuandoSeFinaliza() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var objetivo = vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, ASIGNADO);
        var otro = vinculo(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ASIGNADO);
        adapter.asignar(objetivo);
        adapter.asignar(otro);
        entityManager.flush();

        // Act
        adapter.actualizarEstado(objetivo.id(), FINALIZADO);

        // Assert
        var actualizado = entityManager.find(AsesorProyectoGradoJpaEntity.class, objetivo.id());
        assertThat(actualizado.getEstadoAsesorId()).isEqualTo(FINALIZADO);
        assertThat(actualizado.getAsesorId()).isEqualTo(objetivo.asesor());
        assertThat(actualizado.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(actualizado.getFechaAsignado()).isEqualTo(objetivo.fechaAsignado());
        assertThat(entityManager.find(AsesorProyectoGradoJpaEntity.class, otro.id()).getEstadoAsesorId())
                .isEqualTo(ASIGNADO);
        verify(logger).debug(AsesorProyectoGradoKey.LOG_ACTUALIZADO, objetivo.id(), FINALIZADO);
    }

    @Test
    void debeLiberarCupoYPermitirReasignar_trasFinalizar() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var vinculo = vinculo(asesor, proyectoGrado, ASIGNADO);
        adapter.asignar(vinculo);
        entityManager.flush();

        // Act
        adapter.actualizarEstado(vinculo.id(), FINALIZADO);

        // Assert
        assertThat(adapter.contarPorProyectoGradoYEstado(proyectoGrado, ASIGNADO)).isZero();
        assertThat(adapter.existePorAsesorYProyectoGradoYEstado(asesor, proyectoGrado, ASIGNADO)).isFalse();
        adapter.asignar(vinculo(asesor, proyectoGrado, ASIGNADO));
        entityManager.flush();
        assertThat(adapter.contarPorProyectoGradoYEstado(proyectoGrado, ASIGNADO)).isEqualTo(1);
        assertThat(adapter.contarPorProyectoGradoYEstado(proyectoGrado, FINALIZADO)).isEqualTo(1);
    }

    @Test
    void debeDetectarSoloElVinculoAsignado_cuandoElAsesorEstuvoFinalizadoOEstaEnOtroProyecto() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asignado = UtilUUID.generarNuevoUUID();
        var finalizado = UtilUUID.generarNuevoUUID();
        var enOtroProyecto = UtilUUID.generarNuevoUUID();
        adapter.asignar(vinculo(asignado, proyectoGrado, ASIGNADO));
        adapter.asignar(vinculo(finalizado, proyectoGrado, FINALIZADO));
        adapter.asignar(vinculo(enOtroProyecto, UtilUUID.generarNuevoUUID(), ASIGNADO));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.existePorAsesorYProyectoGradoYEstado(asignado, proyectoGrado, ASIGNADO)).isTrue();
        assertThat(adapter.existePorAsesorYProyectoGradoYEstado(finalizado, proyectoGrado, ASIGNADO)).isFalse();
        assertThat(adapter.existePorAsesorYProyectoGradoYEstado(enOtroProyecto, proyectoGrado, ASIGNADO)).isFalse();
    }
}
