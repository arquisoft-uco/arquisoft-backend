package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorVersionRepositorioExisteFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoBloqueadaPorIdFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.validator.EliminarVersionRepositorioArtefactoValidator;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.event.VersionRepositorioArtefactoEliminacionAutorizadaEvent;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoEnUsoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionRepositorioArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarVersionRepositorioArtefactoUseCaseImplTest {

    @Mock
    private VersionRepositorioArtefactoBloqueadaPorIdFinder versionRepositorioArtefactoBloqueadaPorIdFinder;
    @Mock
    private ArtefactoPorVersionRepositorioExisteFinder artefactoPorVersionRepositorioExisteFinder;
    @Mock
    private EliminarVersionRepositorioArtefactoValidator eliminarVersionRepositorioArtefactoValidator;
    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private EliminarVersionRepositorioArtefactoUseCaseImpl useCase;

    private UUID versionId;
    private UUID repositorioId;
    private EliminacionVersionRepositorioArtefactoDomain eliminacion;
    private VersionRepositorioArtefactoDomain version;

    @BeforeEach
    void setUp() {
        useCase = new EliminarVersionRepositorioArtefactoUseCaseImpl(
                versionRepositorioArtefactoBloqueadaPorIdFinder, artefactoPorVersionRepositorioExisteFinder,
                eliminarVersionRepositorioArtefactoValidator, versionRepositorioArtefactoOutputPort,
                eventPublisher, logger);
        versionId = UtilUUID.generarNuevoUUID();
        repositorioId = UtilUUID.generarNuevoUUID();
        eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(versionId);
        version = VersionRepositorioArtefactoDomain.reconstruir(versionId, "Formato A", 2, repositorioId, "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
        when(versionRepositorioArtefactoBloqueadaPorIdFinder.obtener(versionId)).thenReturn(version);
        when(artefactoPorVersionRepositorioExisteFinder.obtener(versionId)).thenReturn(false);
    }

    @Test
    void debeEliminarYPublicar_cuandoNoEstaEnUso() {
        // Act
        useCase.ejecutar(eliminacion);

        // Assert
        var orden = inOrder(versionRepositorioArtefactoBloqueadaPorIdFinder, artefactoPorVersionRepositorioExisteFinder,
                eliminarVersionRepositorioArtefactoValidator, versionRepositorioArtefactoOutputPort, eventPublisher,
                logger);
        var captor = ArgumentCaptor.forClass(VersionRepositorioArtefactoEliminacionAutorizadaEvent.class);
        orden.verify(logger).info(EliminarVersionRepositorioArtefactoKey.LOG_ELIMINANDO, versionId);
        orden.verify(versionRepositorioArtefactoBloqueadaPorIdFinder, times(1)).obtener(versionId);
        orden.verify(artefactoPorVersionRepositorioExisteFinder, times(1)).obtener(versionId);
        orden.verify(logger).debug(EliminarVersionRepositorioArtefactoKey.LOG_VERIFICACION_ELIMINAR, true, false);
        orden.verify(eliminarVersionRepositorioArtefactoValidator).validar(eliminacion, version, false);
        orden.verify(versionRepositorioArtefactoOutputPort).eliminar(versionId);
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(EliminarVersionRepositorioArtefactoKey.LOG_ELIMINADA, versionId, repositorioId);
        var evento = captor.getValue();
        assertThat(evento.getVersionRepositorioArtefacto()).isEqualTo(versionId);
        assertThat(evento.getNombreDocumento()).isEqualTo("Formato A");
        assertThat(evento.getVersionDocumento()).isEqualTo(2);
        assertThat(evento.getRepositorioArtefacto()).isEqualTo(repositorioId);
    }

    @Test
    void debeNoEliminarNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        doThrow(new VersionRepositorioArtefactoEnUsoException("Formato A", 2, versionId))
                .when(eliminarVersionRepositorioArtefactoValidator).validar(any(), any(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(eliminacion))
                .isInstanceOf(VersionRepositorioArtefactoEnUsoException.class);
        verify(versionRepositorioArtefactoOutputPort, never()).eliminar(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(EliminarVersionRepositorioArtefactoKey.LOG_ELIMINADA), eq(versionId),
                eq(repositorioId));
    }
}
