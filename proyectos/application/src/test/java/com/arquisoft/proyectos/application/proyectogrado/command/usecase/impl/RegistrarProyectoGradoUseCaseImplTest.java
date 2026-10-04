package com.arquisoft.proyectos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.AsignarEstudiantesProyectoGradoUseCase;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoDeFichaExisteFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.result.RegistroProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.proyectos.application.proyectogrado.command.validator.RegistrarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoVigenteException;
import com.arquisoft.proyectos.domain.coordinador.model.ContactoCoordinador;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.RegistroProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoRegistradoEvent;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
class RegistrarProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Mock
    private ProyectoGradoDeFichaExisteFinder proyectoGradoDeFichaExisteFinder;

    @Mock
    private CoordinadorPorIdFinder coordinadorPorIdFinder;

    @Mock
    private RegistrarProyectoGradoValidator validator;

    @Mock
    private AsignarEstudiantesProyectoGradoUseCase asignarEstudiantesProyectoGradoUseCase;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarProyectoGradoUseCaseImpl useCase;

    private final UUID coordinadorId = UUID.randomUUID();
    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UUID.randomUUID(), "Sistema de gestión", coordinadorId);
    private final AgregacionEstudiantesProyectoGradoDomain estudiantes = AgregacionEstudiantesProyectoGradoDomain.crear(
            EstudianteProyectoGradoDomain.crear(proyecto.getId(), List.of(UUID.randomUUID(), UUID.randomUUID())));
    private final RegistroProyectoGradoDomain registro = RegistroProyectoGradoDomain.crear(proyecto, estudiantes);
    private final CoordinadorDomain coordinador = CoordinadorDomain.reconstruir(coordinadorId, "1020",
            "Laura Mesa", "laura.mesa@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);

    @Test
    void debeRegistrarAsignarYPublicarEnOrden_cuandoLaFichaNoTieneProyecto() {
        // Arrange
        when(proyectoGradoDeFichaExisteFinder.obtener(proyecto.getFichaPerfil())).thenReturn(false);
        when(coordinadorPorIdFinder.obtener(coordinadorId)).thenReturn(coordinador);

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RegistroProyectoGradoResult.Registrado.class, registrado -> {
            assertThat(registrado.proyectoGrado()).isEqualTo(proyecto.getId());
            assertThat(registrado.fichaPerfil()).isEqualTo(proyecto.getFichaPerfil());
        });

        var entidad = ArgumentCaptor.forClass(ProyectoGradoEntity.class);
        verify(proyectoGradoOutputPort).registrar(entidad.capture());
        assertThat(entidad.getValue().id()).isEqualTo(proyecto.getId());
        assertThat(entidad.getValue().estadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.EN_PROCESO.getId());

        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(evento.capture());
        assertThat(evento.getValue()).isInstanceOfSatisfying(ProyectoGradoRegistradoEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(proyecto.getId());
            assertThat(publicado.getFichaPerfilId()).isEqualTo(proyecto.getFichaPerfil());
            assertThat(publicado.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(publicado.getCoordinador())
                    .isEqualTo(new ContactoCoordinador("Laura Mesa", "laura.mesa@uco.edu.co"));
        });

        var orden = inOrder(proyectoGradoDeFichaExisteFinder, coordinadorPorIdFinder, validator,
                proyectoGradoOutputPort, asignarEstudiantesProyectoGradoUseCase, eventPublisher);
        orden.verify(proyectoGradoDeFichaExisteFinder, times(1)).obtener(proyecto.getFichaPerfil());
        orden.verify(coordinadorPorIdFinder, times(1)).obtener(coordinadorId);
        orden.verify(validator).validar(coordinadorId, coordinador);
        orden.verify(proyectoGradoOutputPort).registrar(any());
        orden.verify(asignarEstudiantesProyectoGradoUseCase).ejecutar(estudiantes);
        orden.verify(eventPublisher).publish(any());
    }

    @Test
    void debeDevolverDuplicadoSinConsultarAlCoordinadorNiEscribir_cuandoLaFichaYaTieneProyecto() {
        // Arrange
        when(proyectoGradoDeFichaExisteFinder.obtener(proyecto.getFichaPerfil())).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RegistroProyectoGradoResult.Duplicado.class, duplicado ->
                assertThat(duplicado.fichaPerfil()).isEqualTo(proyecto.getFichaPerfil()));
        verify(coordinadorPorIdFinder, never()).obtener(any());
        verify(validator, never()).validar(any(), any());
        verify(proyectoGradoOutputPort, never()).registrar(any());
        verify(asignarEstudiantesProyectoGradoUseCase, never()).ejecutar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeNoPersistirNiAsignarNiPublicar_cuandoElCoordinadorNoEstaVigente() {
        // Arrange
        when(proyectoGradoDeFichaExisteFinder.obtener(proyecto.getFichaPerfil())).thenReturn(false);
        when(coordinadorPorIdFinder.obtener(coordinadorId)).thenReturn(CoordinadorDomain.VACIO);
        doThrow(new CoordinadorNoVigenteException(coordinadorId))
                .when(validator).validar(coordinadorId, CoordinadorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro)).isInstanceOf(CoordinadorNoVigenteException.class);
        verify(proyectoGradoOutputPort, never()).registrar(any());
        verify(asignarEstudiantesProyectoGradoUseCase, never()).ejecutar(any());
        verify(eventPublisher, never()).publish(any());
    }
}
