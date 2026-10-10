package com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesPorIdsFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.EstudiantesVinculadosContadorFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.EstudiantesYaVinculadosFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.AsignarEstudiantesProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.event.EstudiantesProyectoGradoAsignadosEvent;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudiantesNoVigentesException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarEstudiantesProyectoGradoUseCaseImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private EstudiantesVigentesPorIdsFinder estudiantesVigentesPorIdsFinder;

    @Mock
    private EstudiantesVinculadosContadorFinder estudiantesVinculadosContadorFinder;

    @Mock
    private EstudiantesYaVinculadosFinder estudiantesYaVinculadosFinder;

    @Mock
    private AsignarEstudiantesProyectoGradoValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<List<EstudianteProyectoGradoEntity>> vinculos;

    @InjectMocks
    private AsignarEstudiantesProyectoGradoUseCaseImpl useCase;

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UUID.randomUUID(), "Sistema de gestión", UUID.randomUUID());
    private final List<EstudianteDomain> vigentes = List.of(
            estudiante("Ana Gomez", "ana.gomez@soyuco.edu.co"),
            estudiante("Luis Diaz", "luis.diaz@soyuco.edu.co"),
            estudiante("Eva Ruiz", "eva.ruiz@soyuco.edu.co"));
    private final AgregacionEstudiantesProyectoGradoDomain entrada = AgregacionEstudiantesProyectoGradoDomain.crear(
            EstudianteProyectoGradoDomain.crear(proyecto.getId(),
                    vigentes.stream().map(EstudianteDomain::getId).toList()));

    private static EstudianteDomain estudiante(String nombre, String email) {
        return EstudianteDomain.reconstruir(UUID.randomUUID(), "2020", nombre, email,
                Instant.parse("2026-09-01T10:00:00Z"), null);
    }

    private void stubFinders() {
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(estudiantesVigentesPorIdsFinder.obtener(entrada.getEstudiantes())).thenReturn(vigentes);
        when(estudiantesYaVinculadosFinder.obtener(entrada)).thenReturn(List.of());
        when(estudiantesVinculadosContadorFinder.obtener(proyecto.getId())).thenReturn(0L);
    }

    @Test
    void debeVincularLosTresYPublicarConSusContactos_cuandoLaAsignacionEsValida() {
        // Arrange
        stubFinders();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(estudianteProyectoGradoOutputPort).vincular(vinculos.capture());
        assertThat(vinculos.getValue()).hasSize(3)
                .extracting(EstudianteProyectoGradoEntity::proyectoGrado).containsOnly(proyecto.getId());

        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(evento.capture());
        assertThat(evento.getValue()).isInstanceOfSatisfying(EstudiantesProyectoGradoAsignadosEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(proyecto.getId());
            assertThat(publicado.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(publicado.getEstudiantes()).containsExactly(
                    new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                    new ContactoEstudiante("Luis Diaz", "luis.diaz@soyuco.edu.co"),
                    new ContactoEstudiante("Eva Ruiz", "eva.ruiz@soyuco.edu.co"));
        });

        var orden = inOrder(proyectoGradoPorIdFinder, estudiantesVigentesPorIdsFinder, estudiantesYaVinculadosFinder,
                estudiantesVinculadosContadorFinder, validator, estudianteProyectoGradoOutputPort, eventPublisher);
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(proyecto.getId());
        orden.verify(estudiantesVigentesPorIdsFinder, times(1)).obtener(entrada.getEstudiantes());
        orden.verify(estudiantesYaVinculadosFinder, times(1)).obtener(entrada);
        orden.verify(estudiantesVinculadosContadorFinder, times(1)).obtener(proyecto.getId());
        orden.verify(validator).validar(entrada, proyecto, vigentes, List.of(), 0L);
        orden.verify(estudianteProyectoGradoOutputPort).vincular(anyList());
        orden.verify(eventPublisher).publish(any());
    }

    @Test
    void debeNoVincularNiPublicar_cuandoUnaReglaRechazaLaAsignacion() {
        // Arrange
        stubFinders();
        doThrow(new EstudiantesNoVigentesException(List.of(UUID.randomUUID())))
                .when(validator).validar(eq(entrada), any(), anyList(), anyList(), anyLong());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(EstudiantesNoVigentesException.class);
        verify(estudianteProyectoGradoOutputPort, never()).vincular(anyList());
        verify(eventPublisher, never()).publish(any());
    }
}
