package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import java.math.BigDecimal;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection.AvanceEvaluacionProjection;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.AvanceEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.FinalizacionEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ConsolidacionNotasEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.EvaluacionEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection.ContextoAsignacionEvaluacionProjection;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionCommandOutputAdapterTest {

    @Mock
    private EvaluacionCommandRepository repository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private EvaluacionCommandOutputAdapter adapter;

    @Test
    void debeGuardarYLoguear_cuandoRegistra() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        adapter.registrar(new EvaluacionEntity(id, entregable, "PENDIENTE"));

        // Assert
        var captor = ArgumentCaptor.forClass(EvaluacionJpaEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getEntregable()).isEqualTo(entregable);
        assertThat(captor.getValue().getEstadoEvaluacion()).isEqualTo("PENDIENTE");
        verify(logger).debug(EvaluacionKey.LOG_GUARDADA, id);
    }

    @Test
    void debeDelegarExistePorEntregable() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(repository.existsByEntregable(entregable)).thenReturn(true);

        // Act
        var existe = adapter.existePorEntregable(entregable);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeDelegarActualizacionYLoguear_cuandoSeActualizaElEstado() {
        // Arrange
        var evaluacion = UUID.randomUUID();

        // Act
        adapter.actualizarEstado(evaluacion, "EN_PROGRESO");

        // Assert
        verify(repository).actualizarEstado(evaluacion, "EN_PROGRESO");
        verify(logger).debug(EvaluacionKey.LOG_ESTADO_ACTUALIZADO, evaluacion, "EN_PROGRESO");
    }

    @Test
    void debeMapearLaProyeccionAEntity_cuandoSeObtieneElContextoDeAsignacion() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var proyeccion = mock(ContextoAsignacionEvaluacionProjection.class);
        when(proyeccion.getEvaluacion()).thenReturn(evaluacion);
        when(proyeccion.getEstado()).thenReturn("EN_PROGRESO");
        when(repository.buscarContextoAsignacionBloqueado(evaluacion)).thenReturn(Optional.of(proyeccion));

        // Act
        var contexto = adapter.obtenerContextoAsignacionBloqueado(evaluacion);

        // Assert
        assertThat(contexto).hasValueSatisfying(entity -> {
            assertThat(entity.evaluacion()).isEqualTo(evaluacion);
            assertThat(entity.estado()).isEqualTo("EN_PROGRESO");
        });
    }

    @Test
    void debeRetornarVacio_cuandoElContextoDeAsignacionNoExiste() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        when(repository.buscarContextoAsignacionBloqueado(evaluacion)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(adapter.obtenerContextoAsignacionBloqueado(evaluacion)).isEmpty();
    }

    @Test
    void debeDelegarNotasYLoguear_cuandoSeActualizanLasNotas() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var consolidacion = new ConsolidacionNotasEvaluacionEntity(evaluacion, new BigDecimal("4.00"),
                new BigDecimal("3.00"), new BigDecimal("0.5000"), new BigDecimal("0.5000"), 1);

        // Act
        adapter.actualizarNotas(consolidacion);

        // Assert
        verify(repository).actualizarNotas(evaluacion, new BigDecimal("4.00"), new BigDecimal("3.00"),
                new BigDecimal("0.5000"), new BigDecimal("0.5000"), 1);
        verify(logger).debug(EvaluacionKey.LOG_NOTAS_ACTUALIZADAS, evaluacion);
    }

    @Test
    void debeDelegarFinalizacionYLoguear_cuandoSeFinalizaLaEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act
        adapter.finalizar(new FinalizacionEvaluacionEntity(
                evaluacion, "FINALIZADA", new BigDecimal("3.25"), "APROBADA"));

        // Assert
        verify(repository).finalizar(evaluacion, "FINALIZADA", new BigDecimal("3.25"), "APROBADA");
        verify(logger).debug(EvaluacionKey.LOG_FINALIZACION_GUARDADA, evaluacion, "APROBADA");
    }

    @Test
    void debeMapearLaProyeccionDelAvance_cuandoLaEvaluacionExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var proyeccion = mock(AvanceEvaluacionProjection.class);
        when(proyeccion.getEvaluacion()).thenReturn(evaluacion);
        when(proyeccion.getEstado()).thenReturn("EN_PROGRESO");
        when(proyeccion.getEntregable()).thenReturn(entregable);
        when(proyeccion.getProyecto()).thenReturn("Proyecto");
        when(proyeccion.getVersion()).thenReturn(2);
        when(proyeccion.getJuradosVigentes()).thenReturn(3L);
        when(proyeccion.getJuradosEvaluados()).thenReturn(2L);
        when(proyeccion.getAsesorCompleto()).thenReturn(true);
        when(repository.buscarAvance(evaluacion)).thenReturn(Optional.of(proyeccion));

        // Act
        var avance = adapter.obtenerAvance(evaluacion);

        // Assert
        assertThat(avance).contains(new AvanceEvaluacionEntity(
                evaluacion, "EN_PROGRESO", entregable, "Proyecto", 2, 3, 2, true));
    }
}
