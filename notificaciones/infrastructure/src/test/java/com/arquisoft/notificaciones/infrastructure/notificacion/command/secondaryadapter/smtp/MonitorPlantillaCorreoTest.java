package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionProperties;
import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.dao.QueryTimeoutException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MonitorPlantillaCorreoTest {

    private static final String CLAVE = "plantilla.correo-base";

    @Mock
    private RedisFuentePlantillaCorreo fuente;

    @Mock
    private AppLogger logger;

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private AlcanceTraza alcance;

    private MonitorPlantillaCorreo monitor;

    @BeforeEach
    void setUp() {
        var properties = new NotificacionProperties();
        properties.setPlantilla(CLAVE);
        when(gestorTraza.abrir(any())).thenReturn(alcance);
        monitor = new MonitorPlantillaCorreo(fuente, properties, logger, gestorTraza);
    }

    @Test
    void debeRegistrarLaActualizacion_cuandoLaPlantillaCambio() {
        // Arrange
        when(fuente.recargar()).thenReturn(true);

        // Act
        monitor.refrescar();

        // Assert
        verify(logger).info(any(ClaveMensaje.class), any(Object[].class));
        verify(logger, never()).warn(any(ClaveMensaje.class), any(Object[].class));
    }

    @Test
    void debeCallar_cuandoLaPlantillaNoCambio() {
        // Arrange
        when(fuente.recargar()).thenReturn(false);

        // Act
        monitor.refrescar();

        // Assert
        verify(logger, never()).info(any(ClaveMensaje.class), any(Object[].class));
        verify(logger, never()).warn(any(ClaveMensaje.class), any(Object[].class));
    }

    @Test
    void debeAvisarSinPropagar_cuandoLaCandidataNoTieneLosHuecos() {
        // Arrange
        when(fuente.recargar())
                .thenThrow(new PlantillaCorreoNoDisponibleException(HuecosPlantillaCorreo.CUERPO));

        // Act & Assert
        assertThatCode(() -> monitor.refrescar()).doesNotThrowAnyException();
        verify(logger).warn(any(ClaveMensaje.class), any(Object[].class));
    }

    // Spring cancela para siempre una tarea programada que lanza: si el monitor propagara, la
    // plantilla quedaria congelada hasta el proximo despliegue aunque Redis volviera.
    @Test
    void debeAvisarSinPropagar_cuandoRedisNoResponde() {
        // Arrange
        when(fuente.recargar()).thenThrow(new QueryTimeoutException("sin conexion"));

        // Act & Assert
        assertThatCode(() -> monitor.refrescar()).doesNotThrowAnyException();
        verify(logger).warn(any(ClaveMensaje.class), any(Object[].class));
    }

    @Test
    void debeCerrarElAlcanceDeTraza_cuandoLaRecargaFalla() {
        // Arrange
        when(fuente.recargar()).thenThrow(new QueryTimeoutException("sin conexion"));

        // Act
        monitor.refrescar();

        // Assert
        verify(alcance).close();
    }

    // Si abrir() o close() del alcance de traza lanzan, la excepcion no puede escapar de refrescar():
    // Spring no reprograma una @Scheduled que lanza y la plantilla quedaria congelada hasta el
    // proximo despliegue — justo el fallo que este monitor existe para evitar.
    @Test
    void debeNoPropagar_cuandoFallaLaAperturaDelAlcanceDeTraza() {
        // Arrange
        when(gestorTraza.abrir(any())).thenThrow(new IllegalStateException("mdc corrupto"));

        // Act & Assert
        assertThatCode(() -> monitor.refrescar()).doesNotThrowAnyException();
        verify(logger).warn(any(ClaveMensaje.class), any(Object[].class));
    }
}
