package com.arquisoft.shared.catalogoparametros.infrastructure.primaryadapter.arranque;

import com.arquisoft.shared.catalogoparametros.application.command.primaryport.interactor.RefrescarParametrosCatalogoInteractor;
import com.arquisoft.shared.catalogoparametros.application.command.result.RefrescoParametrosCatalogoResult;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosProperties;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.ValidadorConfiguracionCatalogoParametros;
import com.arquisoft.shared.catalogoparametros.infrastructure.exception.CargaInicialParametrosCatalogoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.CatalogoParametrosKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.OrigenTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CargaInicialParametrosCatalogoTest {

    private static final String TOKEN_ID = "0b3f5c1e-5d1a-4c0a-9a55-2f7a3f2d9b11";
    private static final String SECRETO = "secreto-de-prueba-123";

    @Mock
    private ValidadorConfiguracionCatalogoParametros validador;
    @Mock
    private RefrescarParametrosCatalogoInteractor interactor;
    @Mock
    private GestorTraza gestorTraza;
    @Mock
    private AppLogger logger;

    private AlcanceTraza alcance;
    private CargaInicialParametrosCatalogo carga;

    @BeforeEach
    void preparar() {
        alcance = mock(AlcanceTraza.class);
        when(gestorTraza.abrir(any())).thenReturn(alcance);
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test/api/v1", TOKEN_ID, SECRETO,
                Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5),
                Map.of("PARAM_X", "defecto"));
        carga = new CargaInicialParametrosCatalogo(validador, interactor, propiedades, gestorTraza, logger);
    }

    private String argumentosLogueados() {
        return mockingDetails(logger).getInvocations().stream()
                .flatMap(invocacion -> Arrays.stream(invocacion.getArguments()))
                .map(String::valueOf)
                .collect(Collectors.joining("|"));
    }

    @Test
    void debeCargar_cuandoRefrescado() {
        // Arrange
        when(interactor.ejecutar()).thenReturn(new RefrescoParametrosCatalogoResult.Refrescado(3));

        // Act
        carga.cargar();

        // Assert
        verify(interactor).ejecutar();
        verify(logger, never()).error(any(ClaveMensaje.class), any());
    }

    @Test
    void debeAbortar_cuandoConservado() {
        // Arrange
        when(interactor.ejecutar()).thenReturn(new RefrescoParametrosCatalogoResult.Conservado("estado-503"));

        // Act / Assert
        assertThatThrownBy(() -> carga.cargar())
                .isInstanceOfSatisfying(CargaInicialParametrosCatalogoException.class, e -> {
                    assertThat(e.getCodigoError()).isEqualTo(AppCodes.CatalogoParametros.CARGA_INICIAL_FALLIDA);
                    assertThat(e.getMessage()).contains("estado-503");
                });
    }

    @Test
    void debeAbortar_cuandoRechazado() {
        // Arrange
        when(interactor.ejecutar()).thenReturn(new RefrescoParametrosCatalogoResult.Rechazado(401));

        // Act / Assert
        assertThatThrownBy(() -> carga.cargar())
                .isInstanceOfSatisfying(CargaInicialParametrosCatalogoException.class, e -> {
                    assertThat(e.getCodigoError()).isEqualTo(AppCodes.CatalogoParametros.CARGA_INICIAL_FALLIDA);
                    assertThat(e.getMessage()).contains("estado-401");
                });
        verify(logger).error(CatalogoParametrosKey.LOG_CARGA_INICIAL_FALLIDA, "estado-401");
    }

    @Test
    void debeAbortar_cuandoSinConfigurar() {
        // Arrange
        when(interactor.ejecutar()).thenReturn(new RefrescoParametrosCatalogoResult.SinConfigurar());

        // Act / Assert
        assertThatThrownBy(() -> carga.cargar())
                .isInstanceOfSatisfying(CargaInicialParametrosCatalogoException.class,
                        e -> assertThat(e.getMessage()).contains("sin-configurar"));
    }

    @Test
    void debeAbortar_cuandoSuspendido() {
        // Arrange
        when(interactor.ejecutar()).thenReturn(new RefrescoParametrosCatalogoResult.Suspendido());

        // Act / Assert
        assertThatThrownBy(() -> carga.cargar())
                .isInstanceOfSatisfying(CargaInicialParametrosCatalogoException.class,
                        e -> assertThat(e.getMessage()).contains("suspendido"));
    }

    @Test
    void debeLoguearErrorAntesDeAbortar() {
        // Arrange
        when(interactor.ejecutar()).thenReturn(new RefrescoParametrosCatalogoResult.Conservado("estado-503"));

        // Act
        assertThatThrownBy(() -> carga.cargar()).isInstanceOf(CargaInicialParametrosCatalogoException.class);

        // Assert
        verify(logger).error(CatalogoParametrosKey.LOG_CARGA_INICIAL_FALLIDA, "estado-503");
    }

    @Test
    void debeAbrirAlcanceDeTraza_yCerrarloAunqueAborte() {
        // Arrange
        when(interactor.ejecutar()).thenReturn(new RefrescoParametrosCatalogoResult.Suspendido());
        var captor = ArgumentCaptor.forClass(SolicitudTraza.class);

        // Act
        assertThatThrownBy(() -> carga.cargar()).isInstanceOf(CargaInicialParametrosCatalogoException.class);

        // Assert
        var orden = inOrder(gestorTraza, interactor, alcance);
        orden.verify(gestorTraza).abrir(captor.capture());
        orden.verify(interactor).ejecutar();
        orden.verify(alcance).close();
        assertThat(captor.getValue().origen()).isEqualTo(OrigenTraza.PROGRAMADO);
    }

    @Test
    void debeLoguearExito_enApplicationReady() {
        // Arrange
        when(interactor.ejecutar()).thenReturn(new RefrescoParametrosCatalogoResult.Refrescado(3));
        carga.cargar();

        // Act
        carga.registrarCarga();

        // Assert
        verify(logger).info(CatalogoParametrosKey.LOG_CARGA_INICIAL, 3);
        verify(logger).info(CatalogoParametrosKey.LOG_CONFIGURACION, "https://catalogo.test",
                Duration.ofMinutes(5), 1);
    }

    @Test
    void debeAbortarSinEncadenarLaCausa_cuandoElRefrescoLanzaExcepcionInesperada() {
        // Arrange
        when(interactor.ejecutar()).thenThrow(new IllegalArgumentException("Bearer " + SECRETO));

        // Act
        var excepcion = catchAbort();

        // Assert
        assertThat(excepcion.getError().getCodigoError()).isEqualTo(AppCodes.CatalogoParametros.CARGA_INICIAL_FALLIDA);
        assertThat(excepcion.getCause()).isNull();
        assertThat(excepcion.getMessage()).doesNotContain(SECRETO);
        verify(logger).error(CatalogoParametrosKey.LOG_CARGA_INICIAL_FALLIDA, "IllegalArgumentException");
        assertThat(argumentosLogueados()).doesNotContain(SECRETO);
    }

    @Test
    void debeNoFiltrarSecretoNiTokenId_enLogsNiExcepcion() {
        // Arrange
        when(interactor.ejecutar())
                .thenReturn(new RefrescoParametrosCatalogoResult.Refrescado(1))
                .thenReturn(new RefrescoParametrosCatalogoResult.Rechazado(403));
        carga.cargar();
        carga.registrarCarga();

        // Act
        var excepcion = catchAbort();

        // Assert
        var logs = argumentosLogueados();
        assertThat(logs).doesNotContain(SECRETO).doesNotContain(TOKEN_ID).doesNotContain("/api/v1");
        assertThat(excepcion.getMessage()).doesNotContain(SECRETO).doesNotContain(TOKEN_ID);
        assertThat(excepcion.getError().getMessage()).doesNotContain(SECRETO).doesNotContain(TOKEN_ID);
        assertThat(carga.toString()).doesNotContain(SECRETO).doesNotContain(TOKEN_ID);
    }

    private CargaInicialParametrosCatalogoException catchAbort() {
        try {
            carga.cargar();
        } catch (CargaInicialParametrosCatalogoException e) {
            return e;
        }
        throw new AssertionError("se esperaba el aborto de la carga inicial");
    }
}
