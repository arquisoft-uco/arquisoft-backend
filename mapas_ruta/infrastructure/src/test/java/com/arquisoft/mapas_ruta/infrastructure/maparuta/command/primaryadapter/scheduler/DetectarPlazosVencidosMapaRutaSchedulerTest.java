package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.scheduler;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.DetectarPlazosVencidosMapaRutaInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.DetectarPlazosVencidosMapaRutaCommand;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetectarPlazosVencidosMapaRutaSchedulerTest {

    private static final String ZONA = "Pacific/Kiritimati";

    @Mock
    private DetectarPlazosVencidosMapaRutaInteractor interactor;

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private AlcanceTraza alcance;

    @Mock
    private AppLogger logger;

    private DetectarPlazosVencidosMapaRutaScheduler scheduler;

    @BeforeEach
    void preparar() {
        when(gestorTraza.abrir(any(SolicitudTraza.class))).thenReturn(alcance);
        scheduler = new DetectarPlazosVencidosMapaRutaScheduler(interactor, gestorTraza, logger, ZONA);
    }

    @Test
    void debeEjecutarConLaFechaDeLaZonaConfigurada_cuandoSeDispara() {
        // Arrange
        var zona = ZoneId.of(ZONA);
        var antes = LocalDate.now(zona);
        var captor = ArgumentCaptor.forClass(DetectarPlazosVencidosMapaRutaCommand.class);

        // Act
        scheduler.detectar();

        // Assert
        var despues = LocalDate.now(zona);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().hoy()).isBetween(antes, despues);
        verify(alcance).close();
    }

    @Test
    void noDebePropagar_cuandoElInteractorLanzaRuntimeException() {
        // Arrange
        var fallo = new IllegalStateException("fallo inesperado");
        doThrow(fallo).when(interactor).ejecutar(any());

        // Act & Assert
        assertThatCode(() -> scheduler.detectar()).doesNotThrowAnyException();
        verify(logger).error(MapaRutaKey.LOG_DETECCION_INESPERADA, fallo, "fallo inesperado");
        verify(alcance).close();
    }
}
