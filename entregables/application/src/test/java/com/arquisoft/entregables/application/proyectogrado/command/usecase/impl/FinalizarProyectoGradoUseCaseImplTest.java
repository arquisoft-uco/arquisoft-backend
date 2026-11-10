package com.arquisoft.entregables.application.proyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.entregables.application.proyectogrado.command.validator.FinalizarProyectoGradoValidator;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.FinalizacionProyectoGradoDomain;
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
class FinalizarProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private FinalizarProyectoGradoValidator finalizarProyectoGradoValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private FinalizarProyectoGradoUseCaseImpl useCase;

    private final ProyectoGradoDomain proyectoEnProceso = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestion");
    private final FinalizacionProyectoGradoDomain entrada = FinalizacionProyectoGradoDomain.crear(
            proyectoEnProceso.getId());

    @Test
    void debeActualizarElEstadoYRetornarFinalizado_cuandoElProyectoEstaEnProceso() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(proyectoEnProceso.getId())).thenReturn(proyectoEnProceso);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(proyectoGradoPorIdFinder, finalizarProyectoGradoValidator, proyectoGradoOutputPort);
        orden.verify(proyectoGradoPorIdFinder).obtener(proyectoEnProceso.getId());
        orden.verify(finalizarProyectoGradoValidator)
                .validar(new ExistenciaProyectoGrado(proyectoEnProceso.getId(), true));
        orden.verify(proyectoGradoOutputPort)
                .actualizarEstado(proyectoEnProceso.getId(), EstadoProyecto.FINALIZADO.getId());
        assertThat(resultado).isEqualTo(new FinalizacionProyectoGradoResult.Finalizado(proyectoEnProceso.getId()));
    }

    @Test
    void debeRetornarDuplicadoSinActualizar_cuandoElProyectoYaEstaFinalizado() {
        // Arrange
        var finalizado = ProyectoGradoDomain.reconstruir(
                proyectoEnProceso.getId(), "Sistema de gestion", EstadoProyecto.FINALIZADO);
        when(proyectoGradoPorIdFinder.obtener(proyectoEnProceso.getId())).thenReturn(finalizado);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        verify(proyectoGradoOutputPort, never()).actualizarEstado(any(), any());
        assertThat(resultado).isEqualTo(new FinalizacionProyectoGradoResult.Duplicado(proyectoEnProceso.getId()));
    }

    @Test
    void debeNoActualizar_cuandoElValidatorRechazaPorProyectoAusente() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(proyectoEnProceso.getId())).thenReturn(ProyectoGradoDomain.VACIO);
        doThrow(new ProyectoGradoNoEncontradoException(proyectoEnProceso.getId()))
                .when(finalizarProyectoGradoValidator)
                .validar(new ExistenciaProyectoGrado(proyectoEnProceso.getId(), false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(proyectoGradoOutputPort, never()).actualizarEstado(any(), any());
    }
}
