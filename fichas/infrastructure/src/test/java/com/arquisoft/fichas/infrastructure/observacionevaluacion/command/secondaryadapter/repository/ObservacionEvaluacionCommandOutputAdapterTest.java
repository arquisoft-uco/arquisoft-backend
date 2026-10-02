package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.ObservacionEvaluacionEntity;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
}
