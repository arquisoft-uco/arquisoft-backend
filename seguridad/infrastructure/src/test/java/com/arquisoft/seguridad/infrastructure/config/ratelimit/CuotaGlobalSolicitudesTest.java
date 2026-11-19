package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import com.arquisoft.shared.catalogoparametros.application.command.finder.ParametroCatalogoFinder;
import com.arquisoft.shared.catalogoparametros.infrastructure.exception.ParametroCatalogoNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.LimiteSolicitudesKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuotaGlobalSolicitudesTest {

    private static final int RESPALDO = 100;
    private static final String NOMBRE = CuotaGlobalSolicitudes.NOMBRE_PARAMETRO;

    @Mock
    private ParametroCatalogoFinder finder;

    @Mock
    private AppLogger appLogger;

    private CuotaGlobalSolicitudes cuota() {
        return new CuotaGlobalSolicitudes(finder,
                new LimiteSolicitudesProperties(true, RESPALDO, 5, 10), appLogger);
    }

    @Test
    @DisplayName("usa el valor del Catálogo cuando es un entero positivo")
    void debeUsarElValorDelCatalogo_cuandoEsEnteroPositivo() {
        // Arrange
        when(finder.obtener(NOMBRE)).thenReturn("50");

        // Act
        var vigente = cuota().vigente();

        // Assert
        assertThat(vigente).isEqualTo(50);
        verify(appLogger, never()).warn(any(LimiteSolicitudesKey.class), any(Object[].class));
        verify(appLogger).info(LimiteSolicitudesKey.LOG_CUOTA_GLOBAL_ACTUALIZADA, 50);
    }

    @Test
    @DisplayName("recorta los espacios del valor")
    void debeRecortarEspacios_cuandoElValorLosTrae() {
        // Arrange
        when(finder.obtener(NOMBRE)).thenReturn("  50 ");

        // Act & Assert
        assertThat(cuota().vigente()).isEqualTo(50);
    }

    @ParameterizedTest(name = "[{index}] \"{0}\"")
    @ValueSource(strings = {"abc", "50.5", "50.0", "", "   ", "0", "-3"})
    @DisplayName("usa la propiedad y loguea una vez cuando el valor no es una cuota válida")
    void debeUsarLaPropiedadYLoguearUnaVez_cuandoElValorNoEsCuotaValida(String invalido) {
        // Arrange
        when(finder.obtener(NOMBRE)).thenReturn(invalido);
        var cuota = cuota();

        // Act
        var primera = cuota.vigente();
        var segunda = cuota.vigente();

        // Assert
        assertThat(primera).isEqualTo(RESPALDO);
        assertThat(segunda).isEqualTo(RESPALDO);
        verify(appLogger, times(1))
                .warn(LimiteSolicitudesKey.LOG_CUOTA_CATALOGO_INVALIDA, NOMBRE, invalido, RESPALDO);
    }

    @Test
    @DisplayName("loguea una sola vez un mismo valor inválido")
    void debeLoguearUnaVez_cuandoElMismoValorInvalidoSeRepite() {
        // Arrange
        when(finder.obtener(NOMBRE)).thenReturn("abc");
        var cuota = cuota();

        // Act
        cuota.vigente();
        cuota.vigente();
        cuota.vigente();

        // Assert
        verify(appLogger, times(1))
                .warn(LimiteSolicitudesKey.LOG_CUOTA_CATALOGO_INVALIDA, NOMBRE, "abc", RESPALDO);
    }

    @Test
    @DisplayName("loguea de nuevo cuando cambia el valor inválido")
    void debeLoguearDeNuevo_cuandoCambiaElValorInvalido() {
        // Arrange
        when(finder.obtener(NOMBRE)).thenReturn("abc", "xyz");
        var cuota = cuota();

        // Act
        cuota.vigente();
        cuota.vigente();

        // Assert
        verify(appLogger).warn(LimiteSolicitudesKey.LOG_CUOTA_CATALOGO_INVALIDA, NOMBRE, "abc", RESPALDO);
        verify(appLogger).warn(LimiteSolicitudesKey.LOG_CUOTA_CATALOGO_INVALIDA, NOMBRE, "xyz", RESPALDO);
    }

    @Test
    @DisplayName("usa la propiedad y loguea una vez cuando el parámetro no está disponible")
    void debeUsarLaPropiedadYLoguearUnaVez_cuandoElParametroNoEstaDisponible() {
        // Arrange
        when(finder.obtener(NOMBRE)).thenThrow(new ParametroCatalogoNoDisponibleException(NOMBRE));
        var cuota = cuota();

        // Act
        var primera = cuota.vigente();
        var segunda = cuota.vigente();

        // Assert
        assertThat(primera).isEqualTo(RESPALDO);
        assertThat(segunda).isEqualTo(RESPALDO);
        verify(appLogger, times(1)).warn(LimiteSolicitudesKey.LOG_CUOTA_CATALOGO_AUSENTE, NOMBRE, RESPALDO);
    }

    @Test
    @DisplayName("aplica el valor nuevo cuando cambia el Catálogo")
    void debeAplicarElValorNuevo_cuandoCambiaElCatalogo() {
        // Arrange
        when(finder.obtener(NOMBRE)).thenReturn("50", "80");
        var cuota = cuota();

        // Act
        var primera = cuota.vigente();
        var segunda = cuota.vigente();

        // Assert
        assertThat(primera).isEqualTo(50);
        assertThat(segunda).isEqualTo(80);
        verify(appLogger).info(LimiteSolicitudesKey.LOG_CUOTA_GLOBAL_ACTUALIZADA, 50);
        verify(appLogger).info(LimiteSolicitudesKey.LOG_CUOTA_GLOBAL_ACTUALIZADA, 80);
    }

    @Test
    @DisplayName("vuelve a la propiedad cuando un valor válido pasa a inválido")
    void debeVolverALaPropiedad_cuandoUnValorValidoPasaAInvalido() {
        // Arrange
        when(finder.obtener(NOMBRE)).thenReturn("50", "abc");
        var cuota = cuota();

        // Act
        var primera = cuota.vigente();
        var segunda = cuota.vigente();

        // Assert
        assertThat(primera).isEqualTo(50);
        assertThat(segunda).isEqualTo(RESPALDO);
        verify(appLogger).info(LimiteSolicitudesKey.LOG_CUOTA_GLOBAL_ACTUALIZADA, RESPALDO);
    }

    @Test
    @DisplayName("no repite logs mientras el valor no cambia")
    void debeNoRepetirLogsNiParseo_cuandoElValorNoCambia() {
        // Arrange
        when(finder.obtener(NOMBRE)).thenReturn("50");
        var cuota = cuota();

        // Act
        for (var i = 0; i < 5; i++) {
            cuota.vigente();
        }

        // Assert
        verify(appLogger, times(1))
                .info(eq(LimiteSolicitudesKey.LOG_CUOTA_GLOBAL_ACTUALIZADA), any(Object[].class));
    }

    @Test
    @DisplayName("pide el parámetro con su nombre exacto")
    void debePedirElParametroConSuNombreExacto() {
        // Arrange
        when(finder.obtener(NOMBRE)).thenReturn("50");

        // Act
        cuota().vigente();

        // Assert
        verify(finder).obtener("RATE_LIMIT_SOLICITUDES_POR_MINUTO");
    }
}
