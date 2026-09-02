package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonitorLimiteSolicitudesTest {

    @Mock
    private RedisBucketResolver resolver;

    @Mock
    private AppLogger logger;

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private AlcanceTraza alcance;

    @InjectMocks
    private MonitorLimiteSolicitudes monitor;

    @BeforeEach
    void abrirAlcance() {
        when(gestorTraza.abrir(any())).thenReturn(alcance);
    }

    @Test
    @DisplayName("no sondea Redis cuando el limitador no está degradado")
    void debeNoSondearRedis_cuandoNoEstaDegradado() {
        // Arrange
        when(resolver.estaDegradado()).thenReturn(false);

        // Act
        monitor.reintentar();

        // Assert
        verify(resolver, never()).hayConexion();
        verify(resolver, never()).marcarSano();
    }

    @Test
    @DisplayName("restablece la cuota distribuida cuando Redis vuelve")
    void debeRestablecerLaCuotaDistribuida_cuandoRedisVuelve() {
        // Arrange
        when(resolver.estaDegradado()).thenReturn(true);
        when(resolver.hayConexion()).thenReturn(true);

        // Act
        monitor.reintentar();

        // Assert
        verify(resolver).marcarSano();
    }

    @Test
    @DisplayName("sigue con cuota local cuando Redis no responde al reintento")
    void debeSeguirConCuotaLocal_cuandoRedisSigueCaido() {
        // Arrange
        when(resolver.estaDegradado()).thenReturn(true);
        when(resolver.hayConexion()).thenReturn(false);

        // Act
        monitor.reintentar();

        // Assert
        verify(resolver, never()).marcarSano();
    }

    @Test
    @DisplayName("no propaga la excepción cuando el reintento falla")
    void debeNoPropagarExcepcion_cuandoElSondeoFalla() {
        // Arrange
        when(resolver.estaDegradado()).thenThrow(new IllegalStateException("fallo inesperado"));

        // Act & Assert
        assertThatCode(() -> monitor.reintentar())
                .as("Spring cancela para siempre una tarea @Scheduled que lanza: si esta se "
                        + "propagara, el limitador se quedaría con cuota por instancia —y el límite "
                        + "efectivo multiplicado por el número de réplicas— hasta el próximo despliegue")
                .doesNotThrowAnyException();
        verify(logger).error(anyString(), any(Throwable.class));
    }
}
