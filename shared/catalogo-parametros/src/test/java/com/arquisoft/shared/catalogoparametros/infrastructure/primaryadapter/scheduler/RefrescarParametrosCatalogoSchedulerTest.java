package com.arquisoft.shared.catalogoparametros.infrastructure.primaryadapter.scheduler;

import com.arquisoft.shared.catalogoparametros.application.command.primaryport.interactor.RefrescarParametrosCatalogoInteractor;
import com.arquisoft.shared.catalogoparametros.application.command.result.RefrescoParametrosCatalogoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
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
import org.springframework.scheduling.annotation.Scheduled;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefrescarParametrosCatalogoSchedulerTest {

    @Mock
    private RefrescarParametrosCatalogoInteractor interactor;
    @Mock
    private GestorTraza gestorTraza;
    @Mock
    private AppLogger logger;

    private AlcanceTraza alcance;
    private RefrescarParametrosCatalogoScheduler scheduler;

    @BeforeEach
    void preparar() {
        alcance = mock(AlcanceTraza.class);
        lenient().when(gestorTraza.abrir(any())).thenReturn(alcance);
        scheduler = new RefrescarParametrosCatalogoScheduler(interactor, gestorTraza, logger);
    }

    @Test
    void debeAbrirAlcanceYEjecutar() {
        // Arrange
        when(interactor.ejecutar()).thenReturn(new RefrescoParametrosCatalogoResult.Refrescado(1));
        var captor = ArgumentCaptor.forClass(SolicitudTraza.class);

        // Act
        scheduler.refrescar();

        // Assert
        var orden = inOrder(gestorTraza, interactor, alcance);
        orden.verify(gestorTraza).abrir(captor.capture());
        orden.verify(interactor).ejecutar();
        orden.verify(alcance).close();
        assertThat(captor.getValue().origen()).isEqualTo(OrigenTraza.PROGRAMADO);
        verify(logger, never()).error(any(ClaveMensaje.class), any());
    }

    @Test
    void debeCapturarRuntimeException_sinPropagarla() {
        // Arrange
        var secreto = "secreto-de-prueba-123";
        var url = "https://catalogo.test/tokens/0b3f5c1e-5d1a-4c0a-9a55-2f7a3f2d9b11/parametros";
        when(interactor.ejecutar()).thenThrow(new IllegalStateException(url + " " + secreto));

        // Act / Assert
        assertThatCode(() -> scheduler.refrescar()).doesNotThrowAnyException();
        verify(logger).error(CatalogoParametrosKey.LOG_REFRESCO_INESPERADO, "IllegalStateException");
        verify(alcance).close();
    }

    @Test
    void debeDemorarLaPrimeraCorrida_unIntervalo() throws NoSuchMethodException {
        // Arrange / Act
        var anotacion = RefrescarParametrosCatalogoScheduler.class.getMethod("refrescar")
                .getAnnotation(Scheduled.class);

        // Assert
        assertThat(anotacion.initialDelayString()).isEqualTo(anotacion.fixedDelayString())
                .contains("catalogo.parametros.refresh-interval");
    }
}
