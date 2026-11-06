package com.arquisoft.shared.redis.catalogo.messageuco;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgramadorSincronizacionMessageUcoTest {

    @Mock
    private SincronizadorCatalogoMessageUco sincronizador;

    @Mock
    private StringRedisTemplate plantilla;

    @Mock
    private ValueOperations<String, String> operaciones;

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private AlcanceTraza alcance;

    @Mock
    private AppLogger logger;

    private ProgramadorSincronizacionMessageUco programador;

    @BeforeEach
    void preparar() {
        var propiedades = new MessageUcoProperties(Duration.ofMinutes(1), DataSize.ofMegabytes(16));
        programador = new ProgramadorSincronizacionMessageUco(
                sincronizador, plantilla, propiedades, gestorTraza, logger);
        when(gestorTraza.abrir(any())).thenReturn(alcance);
        when(plantilla.opsForValue()).thenReturn(operaciones);
    }

    @Test
    @DisplayName("sincroniza cuando obtiene la reserva")
    void debeSincronizar_cuandoObtieneLaReserva() {
        // Arrange
        reservaDisponible(true);

        // Act
        programador.ejecutar();

        // Assert
        verify(sincronizador).sincronizar();
    }

    @Test
    @DisplayName("no sincroniza cuando otra instancia tiene la reserva")
    void debeNoSincronizar_cuandoOtraInstanciaTieneLaReserva() {
        // Arrange
        reservaDisponible(false);

        // Act
        programador.ejecutar();

        // Assert
        verify(sincronizador, never()).sincronizar();
    }

    @Test
    @DisplayName("no propaga el fallo para que la tarea siga programada")
    void debeNoPropagarElFallo_cuandoMessageUcoCae() {
        // Arrange
        reservaDisponible(true);
        when(sincronizador.sincronizar()).thenThrow(new ResourceAccessException("sin conexión"));

        // Act + Assert
        assertThatCode(programador::ejecutar).doesNotThrowAnyException();
        verify(logger).warn(anyString(), any(Object[].class));
    }

    private void reservaDisponible(boolean disponible) {
        when(operaciones.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(disponible);
    }
}
