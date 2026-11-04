package com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudianteArtefactoExisteFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.validator.RemoverEstudianteArtefactoValidator;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.RemocionEstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.event.EstudianteArtefactoRemovidoEvent;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoEstudiante;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

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
class RemoverEstudianteArtefactoUseCaseImplTest {

    @Mock
    private ArtefactoPorIdFinder artefactoPorIdFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    @Mock
    private EstudiantePorIdFinder estudiantePorIdFinder;
    @Mock
    private EstudianteArtefactoExisteFinder estudianteArtefactoExisteFinder;
    @Mock
    private RemoverEstudianteArtefactoValidator removerEstudianteArtefactoValidator;
    @Mock
    private EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<EstudianteArtefactoRemovidoEvent> eventoCaptor;

    private RemoverEstudianteArtefactoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverEstudianteArtefactoUseCaseImpl(
                artefactoPorIdFinder, asesorPorIdFinder, versionRepositorioArtefactoPorIdFinder,
                estudiantePorIdFinder, estudianteArtefactoExisteFinder, removerEstudianteArtefactoValidator,
                estudianteArtefactoOutputPort, eventPublisher, logger);
    }

    @Test
    void debeConsultarValidarEliminarPublicarYLogear_enOrden_cuandoTodoEsValido() {
        // Arrange
        var asesorId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        var estudianteId = UtilUUID.generarNuevoUUID();
        var entrada = RemocionEstudianteArtefactoDomain.crear(artefactoId, asesorId, estudianteId);
        var version = VersionRepositorioArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Anteproyecto", 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
        var artefacto = ArtefactoDomain.reconstruir(
                artefactoId, UtilUUID.generarNuevoUUID(), version.getId(), asesorId, null);
        var asesor = AsesorDomain.reconstruir(asesorId, "1", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        var estudiante = EstudianteDomain.reconstruir(
                estudianteId, "2", "Laura Gomez", "laura@uco.edu.co", Instant.EPOCH, null);
        var participacion = new ParticipacionEstudianteArtefacto(estudianteId, artefactoId);
        when(artefactoPorIdFinder.obtener(artefactoId)).thenReturn(artefacto);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(asesor);
        when(versionRepositorioArtefactoPorIdFinder.obtener(version.getId())).thenReturn(version);
        when(estudiantePorIdFinder.obtener(estudianteId)).thenReturn(estudiante);
        when(estudianteArtefactoExisteFinder.obtener(participacion)).thenReturn(true);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(
                logger, artefactoPorIdFinder, asesorPorIdFinder, versionRepositorioArtefactoPorIdFinder,
                estudiantePorIdFinder, estudianteArtefactoExisteFinder, removerEstudianteArtefactoValidator,
                estudianteArtefactoOutputPort, eventPublisher);
        orden.verify(logger).info(EstudianteArtefactoKey.LOG_REMOVIENDO, artefactoId, asesorId, estudianteId);
        orden.verify(artefactoPorIdFinder).obtener(artefactoId);
        orden.verify(asesorPorIdFinder).obtener(asesorId);
        orden.verify(versionRepositorioArtefactoPorIdFinder).obtener(version.getId());
        orden.verify(estudiantePorIdFinder).obtener(estudianteId);
        orden.verify(estudianteArtefactoExisteFinder).obtener(participacion);
        orden.verify(removerEstudianteArtefactoValidator)
                .validar(entrada, artefacto, asesor, version, estudiante, true);
        orden.verify(estudianteArtefactoOutputPort).eliminarPorArtefactoYEstudiante(artefactoId, estudianteId);
        orden.verify(eventPublisher).publish(eventoCaptor.capture());
        orden.verify(logger).info(EstudianteArtefactoKey.LOG_REMOVIDO, artefactoId, estudianteId);
        verify(artefactoPorIdFinder, times(1)).obtener(any());
        verify(asesorPorIdFinder, times(1)).obtener(any());
        verify(versionRepositorioArtefactoPorIdFinder, times(1)).obtener(any());
        verify(estudiantePorIdFinder, times(1)).obtener(any());
        verify(estudianteArtefactoExisteFinder, times(1)).obtener(any());
        assertThat(eventoCaptor.getValue().getDocumento()).isEqualTo("Anteproyecto");
        assertThat(eventoCaptor.getValue().getEstudiante())
                .isEqualTo(new ContactoEstudiante("Laura Gomez", "laura@uco.edu.co"));
        assertThat(eventoCaptor.getValue().getAsesor()).isEqualTo(new ContactoAsesor("Ana Perez", "ana@uco.edu.co"));
    }

    @Test
    void debeNoEliminarNiPublicarNiCerrar_cuandoElValidatorLanza() {
        // Arrange
        var asesorId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        var estudianteId = UtilUUID.generarNuevoUUID();
        var entrada = RemocionEstudianteArtefactoDomain.crear(artefactoId, asesorId, estudianteId);
        when(artefactoPorIdFinder.obtener(artefactoId)).thenReturn(ArtefactoDomain.VACIO);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(AsesorDomain.VACIO);
        when(versionRepositorioArtefactoPorIdFinder.obtener(any())).thenReturn(VersionRepositorioArtefactoDomain.VACIO);
        when(estudiantePorIdFinder.obtener(estudianteId)).thenReturn(EstudianteDomain.VACIO);
        when(estudianteArtefactoExisteFinder.obtener(any())).thenReturn(false);
        doThrow(new AsesorNoPropietarioArtefactoException(asesorId, artefactoId))
                .when(removerEstudianteArtefactoValidator)
                .validar(any(), any(), any(), any(), any(), eq(false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
        verify(estudianteArtefactoOutputPort, never()).eliminarPorArtefactoYEstudiante(any(), any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(artefactoId), eq(estudianteId));
    }
}
