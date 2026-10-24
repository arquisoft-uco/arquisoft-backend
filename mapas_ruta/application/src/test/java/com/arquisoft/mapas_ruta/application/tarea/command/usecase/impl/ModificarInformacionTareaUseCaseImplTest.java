package com.arquisoft.mapas_ruta.application.tarea.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.AsesorAsignadoProyectoGradoFinder;
import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.model.AsesorEnProyectoGrado;
import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.DescripcionTareaEnUsoFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.model.DescripcionTareaEnMapaRuta;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.validator.ModificarInformacionTareaValidator;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.ModificacionTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoEncontradaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarInformacionTareaUseCaseImplTest {

    @Mock
    private TareaOutputPort tareaOutputPort;

    @Mock
    private TareaPorIdFinder tareaPorIdFinder;

    @Mock
    private MapaRutaPorIdFinder mapaRutaPorIdFinder;

    @Mock
    private DescripcionTareaEnUsoFinder descripcionTareaEnUsoFinder;

    @Mock
    private AsesorAsignadoProyectoGradoFinder asesorAsignadoProyectoGradoFinder;

    @Mock
    private ModificarInformacionTareaValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarInformacionTareaUseCaseImpl useCase;

    @Test
    void debeConsultarValidarYActualizarEnOrden_cuandoDatosValidos() {
        // Arrange
        var mapa = MapaRutaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        var tarea = TareaDomain.reconstruir(UtilUUID.generarNuevoUUID(), mapa.getId(), "Anterior",
                UtilUUID.generarNuevoUUID(), EstadoItem.NUEVA);
        var modificacion = ModificacionTareaDomain.crear(tarea.getId(), "Redactar capitulo",
                UtilUUID.generarNuevoUUID());
        var descripcionEnMapa = new DescripcionTareaEnMapaRuta(tarea.getId(), mapa.getId(), "Redactar capitulo");
        var asesorEnProyecto = new AsesorEnProyectoGrado(mapa.getProyectoGrado(), modificacion.getAsesor());
        when(tareaPorIdFinder.obtener(tarea.getId())).thenReturn(tarea);
        when(mapaRutaPorIdFinder.obtener(mapa.getId())).thenReturn(mapa);
        when(descripcionTareaEnUsoFinder.obtener(descripcionEnMapa)).thenReturn(false);
        when(asesorAsignadoProyectoGradoFinder.obtener(asesorEnProyecto)).thenReturn(true);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        var orden = inOrder(tareaPorIdFinder, mapaRutaPorIdFinder, descripcionTareaEnUsoFinder,
                asesorAsignadoProyectoGradoFinder, validator, tareaOutputPort);
        orden.verify(tareaPorIdFinder).obtener(tarea.getId());
        orden.verify(mapaRutaPorIdFinder).obtener(mapa.getId());
        orden.verify(descripcionTareaEnUsoFinder).obtener(descripcionEnMapa);
        orden.verify(asesorAsignadoProyectoGradoFinder).obtener(asesorEnProyecto);
        orden.verify(validator).validar(modificacion, tarea, mapa, false, true);
        orden.verify(tareaOutputPort).actualizarInformacion(
                tarea.getId(), "Redactar capitulo", modificacion.getAsesor());
        verify(tareaPorIdFinder, times(1)).obtener(any());
        verify(mapaRutaPorIdFinder, times(1)).obtener(any());
        verify(descripcionTareaEnUsoFinder, times(1)).obtener(any());
        verify(asesorAsignadoProyectoGradoFinder, times(1)).obtener(any());
        verify(logger).info(any(ClaveMensaje.class), eq(tarea.getId()), eq(mapa.getId()));
    }

    @Test
    void debeNoActualizarNiLogearCierre_cuandoValidatorRechaza() {
        // Arrange
        var modificacion = ModificacionTareaDomain.crear(UtilUUID.generarNuevoUUID(), "Redactar capitulo",
                UtilUUID.generarNuevoUUID());
        when(tareaPorIdFinder.obtener(modificacion.getTarea())).thenReturn(TareaDomain.VACIO);
        when(mapaRutaPorIdFinder.obtener(TareaDomain.VACIO.getMapaRuta())).thenReturn(MapaRutaDomain.VACIO);
        doThrow(new TareaNoEncontradaException(modificacion.getTarea())).when(validator)
                .validar(modificacion, TareaDomain.VACIO, MapaRutaDomain.VACIO, false, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion)).isInstanceOf(TareaNoEncontradaException.class);
        verify(descripcionTareaEnUsoFinder, never()).obtener(any());
        verify(asesorAsignadoProyectoGradoFinder, never()).obtener(any());
        verify(tareaOutputPort, never()).actualizarInformacion(any(), any(), any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(modificacion.getTarea()),
                eq(TareaDomain.VACIO.getMapaRuta()));
    }
}
