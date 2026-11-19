package com.arquisoft.mapas_ruta.application.maparuta.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapasRutaConPlazoVencidoFinder;
import com.arquisoft.mapas_ruta.domain.maparuta.DeteccionPlazosVencidosMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.event.MapaRutaPlazoVencidoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetectarPlazosVencidosMapaRutaUseCaseImplTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 19);

    @Mock
    private MapasRutaConPlazoVencidoFinder mapasRutaConPlazoVencidoFinder;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private DetectarPlazosVencidosMapaRutaUseCaseImpl useCase;

    private static MapaRutaDomain mapa(LocalDate fechaFin) {
        return MapaRutaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 8, 1), fechaFin);
    }

    @Test
    void debePublicarUnEventoPorMapaYPasarHoyAlFinder_cuandoHayVencidos() {
        // Arrange
        var primero = mapa(HOY.minusDays(1));
        var segundo = mapa(HOY.minusDays(7));
        var tercero = mapa(HOY.minusDays(30));
        when(mapasRutaConPlazoVencidoFinder.obtener(HOY)).thenReturn(List.of(primero, segundo, tercero));
        var captor = ArgumentCaptor.forClass(MapaRutaPlazoVencidoEvent.class);

        // Act
        useCase.ejecutar(DeteccionPlazosVencidosMapaRutaDomain.crear(HOY));

        // Assert
        verify(mapasRutaConPlazoVencidoFinder, times(1)).obtener(HOY);
        verify(eventPublisher, times(3)).publish(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(MapaRutaPlazoVencidoEvent::getMapaRuta, MapaRutaPlazoVencidoEvent::getProyectoGrado,
                        MapaRutaPlazoVencidoEvent::getFechaFin)
                .containsExactly(
                        tuple(
                                primero.getId(), primero.getProyectoGrado(), primero.getFechaFin()),
                        tuple(
                                segundo.getId(), segundo.getProyectoGrado(), segundo.getFechaFin()),
                        tuple(
                                tercero.getId(), tercero.getProyectoGrado(), tercero.getFechaFin()));
    }

    @Test
    void noDebePublicar_cuandoNoHayVencidos() {
        // Arrange
        when(mapasRutaConPlazoVencidoFinder.obtener(HOY)).thenReturn(List.of());

        // Act
        useCase.ejecutar(DeteccionPlazosVencidosMapaRutaDomain.crear(HOY));

        // Assert
        verify(eventPublisher, never()).publish(any());
    }
}
