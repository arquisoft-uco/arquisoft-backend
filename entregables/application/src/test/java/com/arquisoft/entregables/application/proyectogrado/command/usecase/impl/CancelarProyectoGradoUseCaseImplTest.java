package com.arquisoft.entregables.application.proyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.application.artefacto.command.usecase.PurgarArtefactosProyectoGradoUseCase;
import com.arquisoft.entregables.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.entregables.application.paqueteentregable.command.usecase.PurgarPaquetesProyectoGradoUseCase;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.entregables.application.proyectogrado.command.validator.CancelarProyectoGradoValidator;
import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
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
    private PurgarPaquetesProyectoGradoUseCase purgarPaquetesProyectoGradoUseCase;

    @Mock
    private PurgarArtefactosProyectoGradoUseCase purgarArtefactosProyectoGradoUseCase;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CancelarProyectoGradoUseCaseImpl useCase;

    private final ProyectoGradoDomain proyectoEnProceso = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestion");
    private final CancelacionProyectoGradoDomain entrada = CancelacionProyectoGradoDomain.crear(
            proyectoEnProceso.getId(),
            PurgaPaquetesProyectoGradoDomain.crear(proyectoEnProceso.getId()),
            PurgaArtefactosProyectoGradoDomain.crear(proyectoEnProceso.getId()));

    @Test
    void debeActualizarEstadoYPurgarEnOrden_cuandoNoEstabaCancelado() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(proyectoEnProceso.getId())).thenReturn(proyectoEnProceso);
        when(purgarPaquetesProyectoGradoUseCase.ejecutar(entrada.getPurgaPaquetes()))
                .thenReturn(new PurgaPaquetesProyectoGradoResult(proyectoEnProceso.getId(), 2));
        when(purgarArtefactosProyectoGradoUseCase.ejecutar(entrada.getPurgaArtefactos()))
                .thenReturn(new PurgaArtefactosProyectoGradoResult(proyectoEnProceso.getId(), 3));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(proyectoGradoPorIdFinder, cancelarProyectoGradoValidator, proyectoGradoOutputPort,
                purgarPaquetesProyectoGradoUseCase, purgarArtefactosProyectoGradoUseCase);
        orden.verify(proyectoGradoPorIdFinder).obtener(proyectoEnProceso.getId());
        orden.verify(cancelarProyectoGradoValidator)
                .validar(new ExistenciaProyectoGrado(proyectoEnProceso.getId(), true));
        orden.verify(proyectoGradoOutputPort)
                .actualizarEstado(proyectoEnProceso.getId(), EstadoProyecto.CANCELADO.getId());
        orden.verify(purgarPaquetesProyectoGradoUseCase).ejecutar(entrada.getPurgaPaquetes());
        orden.verify(purgarArtefactosProyectoGradoUseCase).ejecutar(entrada.getPurgaArtefactos());
        assertThat(resultado).isEqualTo(
                new CancelacionProyectoGradoResult.Cancelado(proyectoEnProceso.getId(), 2, 3));
    }

    @Test
    void debeDevolverDuplicadoYPurgarIgual_cuandoYaCancelado() {
        // Arrange
        var cancelado = ProyectoGradoDomain.reconstruir(
                proyectoEnProceso.getId(), "Sistema de gestion", EstadoProyecto.CANCELADO);
        when(proyectoGradoPorIdFinder.obtener(proyectoEnProceso.getId())).thenReturn(cancelado);
        when(purgarPaquetesProyectoGradoUseCase.ejecutar(entrada.getPurgaPaquetes()))
                .thenReturn(new PurgaPaquetesProyectoGradoResult(proyectoEnProceso.getId(), 0));
        when(purgarArtefactosProyectoGradoUseCase.ejecutar(entrada.getPurgaArtefactos()))
                .thenReturn(new PurgaArtefactosProyectoGradoResult(proyectoEnProceso.getId(), 1));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        verify(proyectoGradoOutputPort, never()).actualizarEstado(any(), any());
        verify(purgarPaquetesProyectoGradoUseCase).ejecutar(entrada.getPurgaPaquetes());
        verify(purgarArtefactosProyectoGradoUseCase).ejecutar(entrada.getPurgaArtefactos());
        assertThat(resultado).isEqualTo(
                new CancelacionProyectoGradoResult.Duplicado(proyectoEnProceso.getId(), 0, 1));
    }

    @Test
    void debeNoActualizar_cuandoElValidatorRechazaPorProyectoAusente() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(proyectoEnProceso.getId())).thenReturn(ProyectoGradoDomain.VACIO);
        doThrow(new ProyectoGradoNoEncontradoException(proyectoEnProceso.getId()))
                .when(cancelarProyectoGradoValidator)
                .validar(new ExistenciaProyectoGrado(proyectoEnProceso.getId(), false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(proyectoGradoOutputPort, never()).actualizarEstado(any(), any());
        verify(purgarPaquetesProyectoGradoUseCase, never()).ejecutar(any());
        verify(purgarArtefactosProyectoGradoUseCase, never()).ejecutar(any());
    }
}
