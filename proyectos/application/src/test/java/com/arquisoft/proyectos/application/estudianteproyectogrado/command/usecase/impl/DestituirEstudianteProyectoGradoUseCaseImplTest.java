package com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.EstudianteVinculadoFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.DestituirEstudianteProyectoGradoValidator;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.event.EstudianteProyectoGradoDestituidoEvent;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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
class DestituirEstudianteProyectoGradoUseCaseImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private EstadoActualProyectoGradoFinder estadoActualProyectoGradoFinder;

    @Mock
    private EstudianteVinculadoFinder estudianteVinculadoFinder;

    @Mock
    private DestituirEstudianteProyectoGradoValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private DestituirEstudianteProyectoGradoUseCaseImpl useCase;

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestion", UtilUUID.generarNuevoUUID());
    private final EstadoProyectoGradoDomain estadoActual = EstadoProyectoGradoDomain.crear(proyecto.getId());
    private final DestitucionEstudianteProyectoGradoDomain entrada = DestitucionEstudianteProyectoGradoDomain.crear(
            proyecto.getId(), UtilUUID.generarNuevoUUID(), proyecto.getCoordinador());

    @Test
    void debeDesvincularYPublicarEnOrden_cuandoLaValidacionSeSupera() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(estadoActualProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estadoActual);
        when(estudianteVinculadoFinder.obtener(entrada)).thenReturn(true);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(proyectoGradoPorIdFinder, estadoActualProyectoGradoFinder, estudianteVinculadoFinder, validator,
                estudianteProyectoGradoOutputPort, eventPublisher, logger);
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(proyecto.getId());
        orden.verify(estadoActualProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(estudianteVinculadoFinder, times(1)).obtener(entrada);
        orden.verify(validator).validar(entrada, proyecto, estadoActual, true);
        orden.verify(estudianteProyectoGradoOutputPort)
                .desvincular(entrada.getProyectoGrado(), entrada.getEstudiante());
        orden.verify(eventPublisher).publish(any(DomainEvent.class));
        orden.verify(logger).info(any(ClaveMensaje.class), eq(entrada.getProyectoGrado()),
                eq(entrada.getEstudiante()));
    }

    @Test
    void debePublicarEvento_cuandoDestituye() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(estadoActualProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estadoActual);
        when(estudianteVinculadoFinder.obtener(entrada)).thenReturn(true);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(evento.capture());
        assertThat(evento.getValue()).isInstanceOfSatisfying(EstudianteProyectoGradoDestituidoEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(entrada.getProyectoGrado());
            assertThat(publicado.getEstudianteId()).isEqualTo(entrada.getEstudiante());
        });
    }

    @Test
    void noDebeDesvincularNiPublicarNiCerrar_cuandoElValidatorRechaza() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(estadoActualProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estadoActual);
        when(estudianteVinculadoFinder.obtener(entrada)).thenReturn(false);
        doThrow(new EstudianteNoVinculadoException(entrada.getEstudiante(), entrada.getProyectoGrado()))
                .when(validator).validar(entrada, proyecto, estadoActual, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(EstudianteNoVinculadoException.class);
        verify(estudianteProyectoGradoOutputPort, never()).desvincular(any(), any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(entrada.getProyectoGrado()),
                eq(entrada.getEstudiante()));
    }
}
