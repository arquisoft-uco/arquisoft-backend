package com.arquisoft.mapas_ruta.application.tarea.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.AsesorAsignadoProyectoGradoFinder;
import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.model.AsesorEnProyectoGrado;
import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaPorIdFinder;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaDeMapaRutaExisteFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;
import com.arquisoft.mapas_ruta.application.tarea.command.validator.AsignarTareaValidator;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
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
class AsignarTareaUseCaseImplTest {

    @Mock
    private TareaOutputPort tareaOutputPort;

    @Mock
    private MapaRutaPorIdFinder mapaRutaPorIdFinder;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private TareaDeMapaRutaExisteFinder tareaDeMapaRutaExisteFinder;

    @Mock
    private AsesorAsignadoProyectoGradoFinder asesorAsignadoProyectoGradoFinder;

    @Mock
    private AsignarTareaValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarTareaUseCaseImpl useCase;

    @Test
    void debeRegistrarYDevolverId_cuandoTodoValido() {
        // Arrange
        var mapa = MapaRutaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        var proyecto = ProyectoGradoDomain.reconstruir(mapa.getProyectoGrado(), EstadoProyectoGrado.EN_PROCESO,
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Titulo");
        var tarea = TareaDomain.crear(mapa.getId(), "Redactar capitulo", UtilUUID.generarNuevoUUID());
        var asesorEnProyecto = new AsesorEnProyectoGrado(mapa.getProyectoGrado(), tarea.getAsesor());
        when(mapaRutaPorIdFinder.obtener(mapa.getId())).thenReturn(mapa);
        when(proyectoGradoPorIdFinder.obtener(mapa.getProyectoGrado())).thenReturn(proyecto);
        when(tareaDeMapaRutaExisteFinder.obtener(tarea)).thenReturn(false);
        when(asesorAsignadoProyectoGradoFinder.obtener(asesorEnProyecto)).thenReturn(true);

        // Act
        var id = useCase.ejecutar(tarea);

        // Assert
        assertThat(id).isEqualTo(tarea.getId());
        var orden = inOrder(mapaRutaPorIdFinder, proyectoGradoPorIdFinder, tareaDeMapaRutaExisteFinder,
                asesorAsignadoProyectoGradoFinder, validator, tareaOutputPort);
        orden.verify(mapaRutaPorIdFinder).obtener(mapa.getId());
        orden.verify(proyectoGradoPorIdFinder).obtener(mapa.getProyectoGrado());
        orden.verify(tareaDeMapaRutaExisteFinder).obtener(tarea);
        orden.verify(asesorAsignadoProyectoGradoFinder).obtener(asesorEnProyecto);
        orden.verify(validator).validar(tarea, mapa, proyecto, false, true);
        orden.verify(tareaOutputPort).registrar(argThat((TareaEntity entidad) ->
                entidad.id().equals(tarea.getId())
                        && entidad.mapaRuta().equals(mapa.getId())
                        && entidad.asesor().equals(tarea.getAsesor())
                        && entidad.estadoItem().equals("NUEVA")));
        verify(mapaRutaPorIdFinder, times(1)).obtener(any());
        verify(proyectoGradoPorIdFinder, times(1)).obtener(any());
        verify(tareaDeMapaRutaExisteFinder, times(1)).obtener(any());
        verify(asesorAsignadoProyectoGradoFinder, times(1)).obtener(any());
        verify(logger).info(any(ClaveMensaje.class), eq(tarea.getId()), eq(mapa.getId()));
    }

    @Test
    void noDebeConsultarProyectoNiAsesorNiRegistrar_cuandoMapaNoExiste() {
        // Arrange
        var tarea = TareaDomain.crear(UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID());
        when(mapaRutaPorIdFinder.obtener(tarea.getMapaRuta())).thenReturn(MapaRutaDomain.VACIO);
        doThrow(new MapaRutaNoEncontradoException(tarea.getMapaRuta()))
                .when(validator).validar(tarea, MapaRutaDomain.VACIO, ProyectoGradoDomain.VACIO, false, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(tarea)).isInstanceOf(MapaRutaNoEncontradoException.class);
        verify(proyectoGradoPorIdFinder, never()).obtener(any());
        verify(tareaDeMapaRutaExisteFinder, never()).obtener(any());
        verify(asesorAsignadoProyectoGradoFinder, never()).obtener(any());
        verify(tareaOutputPort, never()).registrar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(tarea.getId()), eq(tarea.getMapaRuta()));
    }
}
