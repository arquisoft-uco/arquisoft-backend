package com.arquisoft.mapas_ruta.application.maparuta.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaDeProyectoExisteFinder;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;
import com.arquisoft.mapas_ruta.application.maparuta.command.validator.AgregarMapaRutaValidator;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.mapas_ruta.domain.maparuta.AgregacionMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.event.MapaRutaAgregadoEvent;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaDuplicadoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
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

import static org.assertj.core.api.Assertions.assertThat;
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
class AgregarMapaRutaUseCaseImplTest {

    @Mock
    private MapaRutaOutputPort mapaRutaOutputPort;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private MapaRutaDeProyectoExisteFinder mapaRutaDeProyectoExisteFinder;

    @Mock
    private AgregarMapaRutaValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarMapaRutaUseCaseImpl useCase;

    private static AgregacionMapaRutaDomain agregacion() {
        var mapaRuta = MapaRutaDomain.crear(
                UtilUUID.generarNuevoUUID(), LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        return AgregacionMapaRutaDomain.crear(mapaRuta, UtilUUID.generarNuevoUUID());
    }

    private static ProyectoGradoDomain proyecto(AgregacionMapaRutaDomain agregacion) {
        return ProyectoGradoDomain.reconstruir(
                agregacion.getMapaRuta().getProyectoGrado(), EstadoProyectoGrado.EN_PROCESO,
                agregacion.getCoordinador(), UtilUUID.generarNuevoUUID(), "Titulo");
    }

    @Test
    void debeRegistrarPublicarYRetornarId_cuandoLaAgregacionEsValida() {
        // Arrange
        var agregacion = agregacion();
        var mapaRuta = agregacion.getMapaRuta();
        var proyecto = proyecto(agregacion);
        when(proyectoGradoPorIdFinder.obtener(mapaRuta.getProyectoGrado())).thenReturn(proyecto);
        when(mapaRutaDeProyectoExisteFinder.obtener(mapaRuta.getProyectoGrado())).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(agregacion);

        // Assert
        assertThat(resultado).isEqualTo(mapaRuta.getId());
        var orden = inOrder(proyectoGradoPorIdFinder, mapaRutaDeProyectoExisteFinder, validator,
                mapaRutaOutputPort, eventPublisher);
        orden.verify(proyectoGradoPorIdFinder).obtener(mapaRuta.getProyectoGrado());
        orden.verify(mapaRutaDeProyectoExisteFinder).obtener(mapaRuta.getProyectoGrado());
        orden.verify(validator).validar(agregacion, proyecto, false);
        orden.verify(mapaRutaOutputPort).registrar(argThat((MapaRutaEntity entity) ->
                entity.id().equals(mapaRuta.getId())
                        && entity.proyectoGrado().equals(mapaRuta.getProyectoGrado())
                        && entity.fechaInicio().equals(mapaRuta.getFechaInicio())
                        && entity.fechaFin().equals(mapaRuta.getFechaFin())));
        orden.verify(eventPublisher).publish(argThat((MapaRutaAgregadoEvent evento) ->
                evento.getMapaRuta().equals(mapaRuta.getId())
                        && evento.getProyectoGrado().equals(mapaRuta.getProyectoGrado())
                        && evento.getFechaInicio().equals(mapaRuta.getFechaInicio())
                        && evento.getFechaFin().equals(mapaRuta.getFechaFin())));
        verify(proyectoGradoPorIdFinder, times(1)).obtener(any());
        verify(mapaRutaDeProyectoExisteFinder, times(1)).obtener(any());
        verify(eventPublisher, times(1)).publish(any());
        verify(logger).info(any(ClaveMensaje.class), eq(mapaRuta.getId()), eq(mapaRuta.getProyectoGrado()));
    }

    @Test
    void debeLanzarYNoConsultarElSegundoFinder_cuandoElProyectoEsVacio() {
        // Arrange
        var agregacion = agregacion();
        var mapaRuta = agregacion.getMapaRuta();
        when(proyectoGradoPorIdFinder.obtener(mapaRuta.getProyectoGrado())).thenReturn(ProyectoGradoDomain.VACIO);
        doThrow(new ProyectoGradoNoEncontradoException(mapaRuta.getProyectoGrado()))
                .when(validator).validar(agregacion, ProyectoGradoDomain.VACIO, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(agregacion))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(mapaRutaDeProyectoExisteFinder, never()).obtener(any());
        verify(mapaRutaOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarSinRegistrarNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        var agregacion = agregacion();
        var mapaRuta = agregacion.getMapaRuta();
        var proyecto = proyecto(agregacion);
        when(proyectoGradoPorIdFinder.obtener(mapaRuta.getProyectoGrado())).thenReturn(proyecto);
        when(mapaRutaDeProyectoExisteFinder.obtener(mapaRuta.getProyectoGrado())).thenReturn(true);
        doThrow(new MapaRutaDuplicadoException(mapaRuta.getProyectoGrado()))
                .when(validator).validar(agregacion, proyecto, true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(agregacion))
                .isInstanceOf(MapaRutaDuplicadoException.class);
        verify(mapaRutaOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(mapaRuta.getId()), eq(mapaRuta.getProyectoGrado()));
    }
}
