package com.arquisoft.artefactos.application.versionartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorVersionArtefactoFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorVersionArtefactoFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.ProyectoGradoNoCongeladoValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAnteriorBloqueadaDeVersionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionDeVersionExisteFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.EstudianteParticipaVersionArtefactoExisteFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoBloqueadaPorIdFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoEsUltimaFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.EliminarVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.RevisionAnteriorNoPermiteEliminarVersionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.EliminacionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesEliminacionVersion;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class EliminarVersionArtefactoUseCaseImplTest {

    private static final String URI = "versiones/abc";

    @Mock
    private ArtefactoBloqueadoPorVersionArtefactoFinder artefactoFinder;
    @Mock
    private RevisionAnteriorBloqueadaDeVersionFinder revisionAnteriorFinder;
    @Mock
    private VersionArtefactoBloqueadaPorIdFinder versionFinder;
    @Mock
    private EstudianteParticipaVersionArtefactoExisteFinder participaFinder;
    @Mock
    private VersionArtefactoEsUltimaFinder esUltimaFinder;
    @Mock
    private RevisionDeVersionExisteFinder revisionDeVersionFinder;
    @Mock
    private EliminarVersionArtefactoValidator validator;
    @Mock
    private AlmacenamientoVersionOutputPort almacenamiento;
    @Mock
    private VersionArtefactoOutputPort versionOutputPort;
    @Mock
    private ProyectoGradoPorVersionArtefactoFinder proyectoGradoPorVersionArtefactoFinder;
    @Mock
    private ProyectoGradoNoCongeladoValidator proyectoGradoNoCongeladoValidator;
    @Mock
    private AppLogger logger;

    private EliminarVersionArtefactoUseCaseImpl useCase;
    private EliminacionVersionArtefactoDomain eliminacion;
    private VersionArtefactoDomain version;
    private ArtefactoDomain artefacto;
    private RevisionAsesorDomain revisionAnterior;

    @BeforeEach
    void setUp() {
        useCase = new EliminarVersionArtefactoUseCaseImpl(artefactoFinder, revisionAnteriorFinder, versionFinder,
                participaFinder, esUltimaFinder, revisionDeVersionFinder, validator, almacenamiento,
                versionOutputPort, proyectoGradoPorVersionArtefactoFinder, proyectoGradoNoCongeladoValidator, logger);
        when(proyectoGradoPorVersionArtefactoFinder.obtener(any())).thenReturn(ProyectoGradoDomain.VACIO);
        var versionId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        eliminacion = EliminacionVersionArtefactoDomain.crear(versionId, UtilUUID.generarNuevoUUID());
        version = VersionArtefactoDomain.reconstruir(versionId, artefactoId, 2, Instant.now(), URI, 1024L);
        artefacto = ArtefactoDomain.reconstruir(artefactoId, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), null);
        revisionAnterior = RevisionAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.EN_PROGRESO, UtilFecha.generarInstanteActual());
        when(artefactoFinder.obtener(versionId)).thenReturn(artefacto);
        when(revisionAnteriorFinder.obtener(versionId)).thenReturn(revisionAnterior);
        when(versionFinder.obtener(versionId)).thenReturn(version);
        when(participaFinder.obtener(eliminacion)).thenReturn(true);
        when(esUltimaFinder.obtener(versionId)).thenReturn(true);
        when(revisionDeVersionFinder.obtener(versionId)).thenReturn(false);
    }

    @Test
    void debeBorrarArchivoYLuegoFila_cuandoCumpleLasReglas() {
        // Act
        useCase.ejecutar(eliminacion);

        // Assert
        var id = eliminacion.getVersionArtefacto();
        var orden = inOrder(logger, artefactoFinder, revisionAnteriorFinder, versionFinder, participaFinder,
                esUltimaFinder, revisionDeVersionFinder, validator, almacenamiento, versionOutputPort, proyectoGradoNoCongeladoValidator);
        orden.verify(logger).info(any(ClaveMensaje.class), eq(id), eq(eliminacion.getSolicitante()));
        orden.verify(artefactoFinder).obtener(id);
        orden.verify(revisionAnteriorFinder).obtener(id);
        orden.verify(versionFinder).obtener(id);
        orden.verify(participaFinder).obtener(eliminacion);
        orden.verify(esUltimaFinder).obtener(id);
        orden.verify(revisionDeVersionFinder).obtener(id);
        var captor = ArgumentCaptor.forClass(PrecondicionesEliminacionVersion.class);
        orden.verify(validator).validar(eq(eliminacion), captor.capture());
        orden.verify(proyectoGradoNoCongeladoValidator).validar(any(ProyectoGradoDomain.class));
        verify(proyectoGradoPorVersionArtefactoFinder, times(1)).obtener(any());
        orden.verify(almacenamiento).eliminarArchivo(URI);
        orden.verify(versionOutputPort).eliminar(id);
        orden.verify(logger).info(any(ClaveMensaje.class), eq(id), eq(2), eq(artefacto.getId()));

        var previas = captor.getValue();
        assertThat(previas.version()).isSameAs(version);
        assertThat(previas.artefacto()).isSameAs(artefacto);
        assertThat(previas.revisionAnterior()).isSameAs(revisionAnterior);
        assertThat(previas.participa()).isTrue();
        assertThat(previas.esUltima()).isTrue();
        assertThat(previas.tieneRevision()).isFalse();
        verify(artefactoFinder, times(1)).obtener(id);
        verify(versionFinder, times(1)).obtener(id);
    }

    @Test
    void debeNoTocarMinioNiFila_cuandoElValidatorRechaza() {
        // Arrange
        doThrow(new RevisionAnteriorNoPermiteEliminarVersionException(2, revisionAnterior))
                .when(validator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(eliminacion))
                .isInstanceOf(RevisionAnteriorNoPermiteEliminarVersionException.class);
        verify(almacenamiento, never()).eliminarArchivo(any());
        verify(versionOutputPort, never()).eliminar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(version.getId()), eq(2), eq(artefacto.getId()));
    }

    @Test
    void debePropagarYNoBorrarLaFila_cuandoMinioFalla() {
        // Arrange
        doThrow(new InfrastructureException("fallo", "COD")).when(almacenamiento).eliminarArchivo(URI);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(eliminacion)).isInstanceOf(InfrastructureException.class);
        verify(versionOutputPort, never()).eliminar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(version.getId()), eq(2), eq(artefacto.getId()));
    }
}
