package com.arquisoft.shared.redis.catalogo;

import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonitorCatalogoRedisTest {

    private static final int DECLARADAS = 221;

    @Mock
    private CatalogoMensajesRedis catalogo;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private MonitorCatalogoRedis monitor;

    @Test
    @DisplayName("no consulta a Redis cuando el catálogo está sano")
    void debeNoConsultarRedis_cuandoElEstadoEsSano() {
        // Arrange
        when(catalogo.estaDegradado()).thenReturn(false);

        // Act
        monitor.reintentar();

        // Assert
        verify(catalogo, never()).hayConexion();
        verify(catalogo, never()).recargar();
    }

    @Test
    @DisplayName("recarga el catálogo y lo marca sano cuando la conexión vuelve")
    void debeRecargarElCatalogo_cuandoLaConexionVuelve() {
        // Arrange
        when(catalogo.estaDegradado()).thenReturn(true);
        when(catalogo.hayConexion()).thenReturn(true);
        when(catalogo.recargar()).thenReturn(new ResultadoCarga(DECLARADAS, DECLARADAS, List.of(), List.of()));

        // Act
        monitor.reintentar();

        // Assert
        verify(catalogo).marcarSano();
    }

    @Test
    @DisplayName("sigue degradado cuando la recarga queda incompleta")
    void debeSeguirDegradado_cuandoLaRecargaQuedaIncompleta() {
        // Arrange
        when(catalogo.estaDegradado()).thenReturn(true);
        when(catalogo.hayConexion()).thenReturn(true);
        when(catalogo.recargar())
                .thenReturn(new ResultadoCarga(DECLARADAS - 1, DECLARADAS, List.of("app.dominio.x.error.y"), List.of()));

        // Act
        monitor.reintentar();

        // Assert
        verify(catalogo, never()).marcarSano();
    }

    @Test
    @DisplayName("no recarga cuando Redis sigue sin responder")
    void debeNoRecargar_cuandoSigueSinConexion() {
        // Arrange
        when(catalogo.estaDegradado()).thenReturn(true);
        when(catalogo.hayConexion()).thenReturn(false);

        // Act
        monitor.reintentar();

        // Assert
        verify(catalogo, never()).recargar();
        verify(catalogo, never()).marcarSano();
    }

    @Test
    @DisplayName("no propaga la excepción cuando el reintento falla")
    void debeNoPropagarExcepcion_cuandoElPingFalla() {
        // Arrange
        when(catalogo.estaDegradado()).thenThrow(new IllegalStateException("fallo inesperado"));

        // Act & Assert
        assertThatCode(() -> monitor.reintentar())
                .as("Spring cancela para siempre una tarea @Scheduled que lanza: si esta se "
                        + "propagara, el catálogo se quedaría degradado hasta el próximo despliegue")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("registra el error cuando el reintento falla")
    void debeRegistrarError_cuandoElPingFalla() {
        // Arrange
        when(catalogo.estaDegradado()).thenThrow(new IllegalStateException("fallo inesperado"));

        // Act
        monitor.reintentar();

        // Assert
        verify(logger).error(anyString(), any(Throwable.class));
    }
}
