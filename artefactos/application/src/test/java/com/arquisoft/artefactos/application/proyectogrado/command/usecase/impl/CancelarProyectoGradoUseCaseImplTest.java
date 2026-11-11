package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.CancelarProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelarProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;
    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    @Mock
    private CancelarProyectoGradoValidator cancelarProyectoGradoValidator;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private CancelarProyectoGradoUseCaseImpl useCase;

    @Test
    void debeMarcarCancelado_cuandoNoEstaCancelado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var canceladoEn = Instant.parse("2026-11-01T10:00:00Z");
        var cancelacion = CancelacionProyectoGradoDomain.crear(id, canceladoEn);
        when(proyectoGradoPorIdFinder.obtener(id))
                .thenReturn(ProyectoGradoDomain.reconstruir(id, "Proyecto", UtilFecha.VACIO, UtilFecha.VACIO, null));

        // Act
        var resultado = useCase.ejecutar(cancelacion);

        // Assert
        var orden = inOrder(proyectoGradoPorIdFinder, cancelarProyectoGradoValidator, proyectoGradoOutputPort);
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(cancelarProyectoGradoValidator).validar(new ExistenciaProyectoGrado(id, true));
        orden.verify(proyectoGradoOutputPort).marcarCancelado(id, canceladoEn);
        assertThat(resultado).isEqualTo(new CancelacionProyectoGradoResult.Cancelado(id));
    }

    @Test
    void debeDevolverYaCancelado_cuandoReentrega() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cancelacion = CancelacionProyectoGradoDomain.crear(id, Instant.parse("2026-11-02T10:00:00Z"));
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.reconstruir(
                id, "Proyecto", UtilFecha.VACIO, UtilFecha.VACIO, Instant.parse("2026-11-01T10:00:00Z")));

        // Act
        var resultado = useCase.ejecutar(cancelacion);

        // Assert
        assertThat(resultado).isEqualTo(new CancelacionProyectoGradoResult.YaCancelado(id));
        verify(proyectoGradoOutputPort, never()).marcarCancelado(any(), any());
    }

    @Test
    void debeNoMarcar_cuandoValidatorRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cancelacion = CancelacionProyectoGradoDomain.crear(id, Instant.parse("2026-11-01T10:00:00Z"));
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);
        doThrow(new ProyectoGradoNoEncontradoException(id)).when(cancelarProyectoGradoValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cancelacion)).isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(proyectoGradoOutputPort, never()).marcarCancelado(any(), any());
    }
}
