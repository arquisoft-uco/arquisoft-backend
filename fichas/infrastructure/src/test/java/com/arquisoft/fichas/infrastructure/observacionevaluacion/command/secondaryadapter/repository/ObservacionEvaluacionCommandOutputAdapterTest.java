package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.ObservacionEvaluacionEntity;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.PertenenciaObservacionEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity.ObservacionEvaluacionJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionEvaluacionCommandOutputAdapterTest {

    @Mock
    private ObservacionEvaluacionCommandRepository repository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ObservacionEvaluacionCommandOutputAdapter adapter;

    @Test
    void debeGuardarJpaEntityYRegistrarLog_cuandoRegistraObservacion() {
        // Arrange
        var observacion = new ObservacionEvaluacionEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Observación válida");

        // Act
        adapter.registrarObservacion(observacion);

        // Assert
        var captor = ArgumentCaptor.forClass(ObservacionEvaluacionJpaEntity.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(observacion.id());
        assertThat(captor.getValue().getEvaluacionFichaPerfilId()).isEqualTo(observacion.evaluacionFichaPerfil());
        assertThat(captor.getValue().getObservacion()).isEqualTo(observacion.observacion());
        verify(logger).debug(any(ClaveMensaje.class), eq(observacion.id()));
    }

    @Test
    void debeRetornarLaExistenciaDelRepositorio_cuandoConsultaPorEvaluacionYObservacion() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        when(repository.existsByEvaluacionFichaPerfilIdAndObservacion(evaluacionFichaPerfil, "Observación válida"))
                .thenReturn(true);

        // Act
        var existe = adapter.existePorEvaluacionYObservacion(evaluacionFichaPerfil, "Observación válida");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarLaPertenenciaDelRepositorio_cuandoConsultaPorObservacionYRepresentante() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var pertenencia = Optional.of(new PertenenciaObservacionEvaluacionEntity(
                UtilUUID.generarNuevoUUID(), true, "EN_EVALUACION"));
        when(repository.obtenerPertenencia(observacionEvaluacion, representanteComite)).thenReturn(pertenencia);

        // Act
        var resultado = adapter.obtenerPertenencia(observacionEvaluacion, representanteComite);

        // Assert
        assertThat(resultado).isSameAs(pertenencia);
        verifyNoInteractions(logger);
    }

    @Test
    void debeRetornarLaDuplicidadDelRepositorio_cuandoConsultaOtraConMismoTexto() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        when(repository.existeOtraConMismoTexto(observacionEvaluacion, "Observación válida")).thenReturn(true);

        // Act
        var existe = adapter.existeOtraConMismoTexto(observacionEvaluacion, "Observación válida");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeActualizarYRegistrarLog_cuandoActualizaObservacion() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();

        // Act
        adapter.actualizarObservacion(observacionEvaluacion, "Texto corregido");

        // Assert
        verify(repository).actualizarObservacion(observacionEvaluacion, "Texto corregido");
        verify(logger).debug(any(ClaveMensaje.class), eq(observacionEvaluacion));
    }
}
