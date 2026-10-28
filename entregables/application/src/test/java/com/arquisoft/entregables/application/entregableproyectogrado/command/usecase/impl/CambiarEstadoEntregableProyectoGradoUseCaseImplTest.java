package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.CambiarEstadoEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.CierreEntregableNoPermitidoException;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
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
class CambiarEstadoEntregableProyectoGradoUseCaseImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Mock
    private EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;

    @Mock
    private CambiarEstadoEntregableProyectoGradoValidator cambiarEstadoEntregableProyectoGradoValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoEntregableProyectoGradoUseCaseImpl useCase;

    @Test
    void debeActualizarElEstado_cuandoTodoEsValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(id, "CERRADO");
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, UtilUUID.generarNuevoUUID(), 1,
                UtilUUID.generarNuevoUUID(), EstadoEntregable.APROBADO);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(entregableProyectoGradoPorIdFinder, cambiarEstadoEntregableProyectoGradoValidator,
                entregableProyectoGradoOutputPort, logger);
        orden.verify(entregableProyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(cambiarEstadoEntregableProyectoGradoValidator).validar(cambio, entregable);
        orden.verify(entregableProyectoGradoOutputPort).actualizarEstado(id, "CERRADO");
        orden.verify(logger).info(EntregableProyectoGradoKey.LOG_ESTADO_CAMBIADO, id, "APROBADO", "CERRADO");
    }

    @Test
    void debePropagarLaExcepcionYNoActualizar_cuandoElValidatorRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(id, "CERRADO");
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, UtilUUID.generarNuevoUUID(), 1,
                UtilUUID.generarNuevoUUID(), EstadoEntregable.RECHAZADO);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        doThrow(new CierreEntregableNoPermitidoException(id, EstadoEntregable.RECHAZADO))
                .when(cambiarEstadoEntregableProyectoGradoValidator).validar(cambio, entregable);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio)).isInstanceOf(CierreEntregableNoPermitidoException.class);
        verify(entregableProyectoGradoOutputPort, never()).actualizarEstado(any(), any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(id), eq("RECHAZADO"), eq("CERRADO"));
    }
}
