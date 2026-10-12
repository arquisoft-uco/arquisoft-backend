package com.arquisoft.mapas_ruta.application.maparuta.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaPorIdFinder;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.command.validator.CambiarFechaFinMapaRutaValidator;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.mapas_ruta.domain.maparuta.CambioFechaFinMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.event.MapaRutaFechaFinCambiadaEvent;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.FechaFinMapaRutaNoPosteriorException;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarFechaFinMapaRutaUseCaseImplTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 10, 1);
    private static final LocalDate FIN_ACTUAL = LocalDate.of(2026, 12, 1);
    private static final LocalDate FIN_NUEVA = LocalDate.of(2026, 12, 20);

    @Mock
    private MapaRutaOutputPort mapaRutaOutputPort;

    @Mock
    private MapaRutaPorIdFinder mapaRutaPorIdFinder;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private CambiarFechaFinMapaRutaValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarFechaFinMapaRutaUseCaseImpl useCase;

    @Test
    void debeActualizarYPublicarConFechaAnterior_cuandoElCambioEsValido() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var mapa = MapaRutaDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), INICIO, FIN_ACTUAL);
        var proyecto = ProyectoGradoDomain.reconstruir(
                mapa.getProyectoGrado(), EstadoProyectoGrado.EN_PROCESO, coordinador,
                UtilUUID.generarNuevoUUID(), "Titulo");
        var cambio = CambioFechaFinMapaRutaDomain.crear(mapa.getId(), FIN_NUEVA, coordinador);
        when(mapaRutaPorIdFinder.obtener(mapa.getId())).thenReturn(mapa);
        when(proyectoGradoPorIdFinder.obtener(mapa.getProyectoGrado())).thenReturn(proyecto);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(mapaRutaPorIdFinder, proyectoGradoPorIdFinder, validator, mapaRutaOutputPort,
                eventPublisher);
        orden.verify(mapaRutaPorIdFinder).obtener(mapa.getId());
        orden.verify(proyectoGradoPorIdFinder).obtener(mapa.getProyectoGrado());
        orden.verify(validator).validar(cambio, mapa, proyecto);
        orden.verify(mapaRutaOutputPort).actualizarFechaFin(mapa.getId(), FIN_NUEVA);
        orden.verify(eventPublisher).publish(argThat((MapaRutaFechaFinCambiadaEvent evento) ->
                evento.getMapaRuta().equals(mapa.getId())
                        && evento.getProyectoGrado().equals(mapa.getProyectoGrado())
                        && evento.getFechaInicio().equals(INICIO)
                        && evento.getFechaFinAnterior().equals(FIN_ACTUAL)
                        && evento.getFechaFin().equals(FIN_NUEVA)));
        verify(mapaRutaPorIdFinder, times(1)).obtener(any());
        verify(proyectoGradoPorIdFinder, times(1)).obtener(any());
        verify(eventPublisher, times(1)).publish(any());
        verify(logger).info(any(ClaveMensaje.class), eq(mapa.getId()), eq(FIN_NUEVA));
    }

    @Test
    void noDebeConsultarProyectoNiPersistirNiPublicar_cuandoValidadorRechaza() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var mapa = MapaRutaDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), INICIO, FIN_ACTUAL);
        var proyecto = ProyectoGradoDomain.reconstruir(
                mapa.getProyectoGrado(), EstadoProyectoGrado.EN_PROCESO, coordinador,
                UtilUUID.generarNuevoUUID(), "Titulo");
        var cambio = CambioFechaFinMapaRutaDomain.crear(mapa.getId(), INICIO, coordinador);
        when(mapaRutaPorIdFinder.obtener(mapa.getId())).thenReturn(mapa);
        when(proyectoGradoPorIdFinder.obtener(mapa.getProyectoGrado())).thenReturn(proyecto);
        doThrow(new FechaFinMapaRutaNoPosteriorException(INICIO, INICIO))
                .when(validator).validar(cambio, mapa, proyecto);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(FechaFinMapaRutaNoPosteriorException.class);
        verify(mapaRutaOutputPort, never()).actualizarFechaFin(any(), any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(mapa.getId()), eq(INICIO));
    }

    @Test
    void noDebeConsultarProyecto_cuandoMapaNoExiste() {
        // Arrange
        var cambio = CambioFechaFinMapaRutaDomain.crear(
                UtilUUID.generarNuevoUUID(), FIN_NUEVA, UtilUUID.generarNuevoUUID());
        when(mapaRutaPorIdFinder.obtener(cambio.getMapaRuta())).thenReturn(MapaRutaDomain.VACIO);
        doThrow(new MapaRutaNoEncontradoException(cambio.getMapaRuta()))
                .when(validator).validar(cambio, MapaRutaDomain.VACIO, ProyectoGradoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(MapaRutaNoEncontradoException.class);
        verify(proyectoGradoPorIdFinder, never()).obtener(any());
        verify(mapaRutaOutputPort, never()).actualizarFechaFin(any(), any());
        verify(eventPublisher, never()).publish(any());
    }
}
