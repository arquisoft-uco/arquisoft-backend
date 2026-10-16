package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
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
class AsesorProyectoGradoCommandOutputAdapterTest {

    private static final String ASIGNADO = "ASIGNADO";
    private static final String FINALIZADO = "FINALIZADO";
    private static final Instant T1 = Instant.parse("2026-10-01T10:00:00Z");
    private static final Instant T2 = Instant.parse("2026-10-02T10:00:00Z");
    private static final Instant T3 = Instant.parse("2026-10-03T10:00:00Z");

    @Autowired
    private AsesorProyectoGradoCommandRepository asesorProyectoGradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private static AsesorProyectoGradoEntity fila(UUID asesor, UUID proyectoGrado, String estado, Instant fecha) {
        return new AsesorProyectoGradoEntity(UtilUUID.generarNuevoUUID(), asesor, proyectoGrado, estado, fecha);
    }

    private AsesorProyectoGradoCommandOutputAdapter adapter() {
        return new AsesorProyectoGradoCommandOutputAdapter(asesorProyectoGradoCommandRepository, logger);
    }

    @Test
    void debePersistirLaFilaConTodosSusCampos_cuandoSeRegistra() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vinculo = fila(UtilUUID.generarNuevoUUID(), proyectoGrado, ASIGNADO, T1);

        // Act
        adapter.registrar(vinculo);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(AsesorProyectoGradoJpaEntity.class, vinculo.id());
        assertThat(guardado.getAsesorId()).isEqualTo(vinculo.asesor());
        assertThat(guardado.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(guardado.getEstadoAsesorId()).isEqualTo(ASIGNADO);
        assertThat(guardado.getFechaAsignado()).isEqualTo(T1);
        verify(logger).debug(AsesorProyectoGradoKey.LOG_GUARDADO, vinculo.id(), proyectoGrado);
    }

    @Test
    void debeRegistrarFilaNueva_sinModificarLaAnterior() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var asignado = fila(asesor, proyectoGrado, ASIGNADO, T1);
        var finalizado = fila(asesor, proyectoGrado, FINALIZADO, T2);
        adapter.registrar(asignado);
        entityManager.flush();

        // Act
        adapter.registrar(finalizado);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var anterior = entityManager.find(AsesorProyectoGradoJpaEntity.class, asignado.id());
        assertThat(anterior.getEstadoAsesorId()).isEqualTo(ASIGNADO);
        assertThat(anterior.getFechaAsignado()).isEqualTo(T1);
        var nueva = entityManager.find(AsesorProyectoGradoJpaEntity.class, finalizado.id());
        assertThat(nueva.getEstadoAsesorId()).isEqualTo(FINALIZADO);
        assertThat(nueva.getFechaAsignado()).isEqualTo(T2);
    }

    @Test
    void debeObtenerLaFilaMasReciente_cuandoHayHistorial() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var reciente = fila(asesor, proyectoGrado, FINALIZADO, T2);
        adapter.registrar(fila(asesor, proyectoGrado, ASIGNADO, T1));
        adapter.registrar(reciente);
        entityManager.flush();
        entityManager.clear();

        // Act
        var obtenido = adapter.obtenerVigentePorAsesorYProyectoGrado(asesor, proyectoGrado);

        // Assert
        assertThat(obtenido).contains(reciente);
    }

    @Test
    void debeRetornarVacio_cuandoElAsesorNoTieneFilaEnElProyecto() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        adapter.registrar(fila(asesor, UtilUUID.generarNuevoUUID(), ASIGNADO, T1));
        adapter.registrar(fila(UtilUUID.generarNuevoUUID(), proyectoGrado, ASIGNADO, T1));
        entityManager.flush();
        entityManager.clear();

        // Act
        var obtenido = adapter.obtenerVigentePorAsesorYProyectoGrado(asesor, proyectoGrado);

        // Assert
        assertThat(obtenido).isEmpty();
    }

    @Test
    void debeLiberarCupoYPermitirReasignar_trasFinalizar() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        adapter.registrar(fila(asesor, proyectoGrado, ASIGNADO, T1));
        adapter.registrar(fila(asesor, proyectoGrado, FINALIZADO, T2));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.contarPorProyectoGradoConEstadoVigente(proyectoGrado, ASIGNADO)).isZero();
        assertThat(adapter.existePorAsesorYProyectoGradoConEstadoVigente(asesor, proyectoGrado, ASIGNADO)).isFalse();
        assertThat(adapter.contarPorProyectoGradoConEstadoVigente(proyectoGrado, FINALIZADO)).isEqualTo(1);
        assertThat(adapter.existePorAsesorYProyectoGradoConEstadoVigente(asesor, proyectoGrado, FINALIZADO)).isTrue();
    }

    @Test
    void debeVolverAContarAsignado_cuandoSeReasignaTrasFinalizar() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        adapter.registrar(fila(asesor, proyectoGrado, ASIGNADO, T1));
        adapter.registrar(fila(asesor, proyectoGrado, FINALIZADO, T2));
        adapter.registrar(fila(asesor, proyectoGrado, ASIGNADO, T3));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.contarPorProyectoGradoConEstadoVigente(proyectoGrado, ASIGNADO)).isEqualTo(1);
        assertThat(adapter.existePorAsesorYProyectoGradoConEstadoVigente(asesor, proyectoGrado, ASIGNADO)).isTrue();
        assertThat(adapter.contarPorProyectoGradoConEstadoVigente(proyectoGrado, FINALIZADO)).isZero();
    }

    @Test
    void debeContarCadaAsesorPorSuFilaMasReciente_cuandoHayVarios() {
        // Arrange
        var adapter = adapter();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var finalizado = UtilUUID.generarNuevoUUID();
        var asignado = UtilUUID.generarNuevoUUID();
        adapter.registrar(fila(finalizado, proyectoGrado, ASIGNADO, T1));
        adapter.registrar(fila(finalizado, proyectoGrado, FINALIZADO, T2));
        adapter.registrar(fila(asignado, proyectoGrado, ASIGNADO, T1));
        adapter.registrar(fila(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ASIGNADO, T1));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.contarPorProyectoGradoConEstadoVigente(proyectoGrado, ASIGNADO)).isEqualTo(1);
        assertThat(adapter.contarPorProyectoGradoConEstadoVigente(proyectoGrado, FINALIZADO)).isEqualTo(1);
        assertThat(adapter.existePorAsesorYProyectoGradoConEstadoVigente(asignado, proyectoGrado, ASIGNADO)).isTrue();
        assertThat(adapter.existePorAsesorYProyectoGradoConEstadoVigente(finalizado, proyectoGrado, ASIGNADO))
                .isFalse();
    }
}
