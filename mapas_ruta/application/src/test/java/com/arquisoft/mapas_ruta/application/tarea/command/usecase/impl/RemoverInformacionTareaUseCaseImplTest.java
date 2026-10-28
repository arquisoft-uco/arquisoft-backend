package com.arquisoft.mapas_ruta.application.tarea.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.validator.RemoverInformacionTareaValidator;
import com.arquisoft.mapas_ruta.domain.tarea.RemocionTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoRemovibleException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class RemoverInformacionTareaUseCaseImplTest {

    @Mock
    private TareaOutputPort tareaOutputPort;

    @Mock
    private TareaPorIdFinder tareaPorIdFinder;

    @Mock
    private RemoverInformacionTareaValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverInformacionTareaUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoValidacionPasa() {
        // Arrange
        var remocion = RemocionTareaDomain.crear(UtilUUID.generarNuevoUUID());
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var tarea = TareaDomain.reconstruir(remocion.getTarea(), mapaRuta,
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), EstadoItem.NUEVA);
        when(tareaPorIdFinder.obtener(remocion.getTarea())).thenReturn(tarea);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(tareaPorIdFinder, validator, tareaOutputPort);
        orden.verify(tareaPorIdFinder).obtener(remocion.getTarea());
        orden.verify(validator).validar(remocion, tarea);
        orden.verify(tareaOutputPort).eliminar(remocion.getTarea());
        verify(tareaPorIdFinder, times(1)).obtener(any());
        verify(logger).info(any(ClaveMensaje.class), eq(remocion.getTarea()), eq(mapaRuta));
    }

    @Test
    void debeNoEliminar_cuandoValidatorRechaza() {
        // Arrange
        var remocion = RemocionTareaDomain.crear(UtilUUID.generarNuevoUUID());
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var tarea = TareaDomain.reconstruir(remocion.getTarea(), mapaRuta,
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), EstadoItem.FINALIZADA);
        when(tareaPorIdFinder.obtener(remocion.getTarea())).thenReturn(tarea);
        doThrow(new TareaNoRemovibleException(remocion.getTarea(), EstadoItem.FINALIZADA))
                .when(validator).validar(remocion, tarea);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(TareaNoRemovibleException.class);
        verify(tareaOutputPort, never()).eliminar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(remocion.getTarea()), eq(mapaRuta));
    }
}
