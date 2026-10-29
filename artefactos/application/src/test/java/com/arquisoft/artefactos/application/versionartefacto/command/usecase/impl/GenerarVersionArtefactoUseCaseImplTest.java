package com.arquisoft.artefactos.application.versionartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorIdFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudianteArtefactoExisteFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionVigenteUltimaVersionFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.AnalisisCargaFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.CargaPendienteFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.UltimoNumeroVersionArtefactoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.GenerarVersionArtefactoContenidoValidator;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.GenerarVersionArtefactoValidator;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.AnalisisArchivoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.event.VersionArtefactoGeneradaEvent;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaModificadaException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaNoCorrespondeException;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesGeneracionVersion;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarVersionArtefactoUseCaseImplTest {

    private static final long MAXIMO = 5_242_880L;
    private static final String TIPO = "application/pdf";
    private static final String ETAG = "etag-1";

    @Mock
    private ArtefactoBloqueadoPorIdFinder artefactoBloqueadoPorIdFinder;
    @Mock
    private EstudianteArtefactoExisteFinder estudianteArtefactoExisteFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    @Mock
    private CargaPendienteFinder cargaPendienteFinder;
    @Mock
    private AnalisisCargaFinder analisisCargaFinder;
    @Mock
    private UltimoNumeroVersionArtefactoFinder ultimoNumeroVersionArtefactoFinder;
    @Mock
    private RevisionVigenteUltimaVersionFinder revisionVigenteUltimaVersionFinder;
    @Mock
    private TamanoMaximoArchivoFinder tamanoMaximoArchivoFinder;
    @Mock
    private GenerarVersionArtefactoValidator validator;
    @Mock
    private GenerarVersionArtefactoContenidoValidator contenidoValidator;
    @Mock
    private AlmacenamientoVersionOutputPort almacenamiento;
    @Mock
    private VersionArtefactoOutputPort versionOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private GenerarVersionArtefactoUseCaseImpl useCase;
    private GeneracionVersionArtefactoDomain generacion;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private VersionRepositorioArtefactoDomain versionRepositorio;
    private CargaPendienteDomain carga;
    private AnalisisArchivoDomain analisis;
    private RevisionAsesorDomain revisionVigente;

    @BeforeEach
    void setUp() {
        useCase = new GenerarVersionArtefactoUseCaseImpl(artefactoBloqueadoPorIdFinder,
                estudianteArtefactoExisteFinder, asesorPorIdFinder, versionRepositorioArtefactoPorIdFinder,
                cargaPendienteFinder, analisisCargaFinder, ultimoNumeroVersionArtefactoFinder,
                revisionVigenteUltimaVersionFinder, tamanoMaximoArchivoFinder, validator,
                contenidoValidator, almacenamiento, versionOutputPort, eventPublisher, logger);
        generacion = GeneracionVersionArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var asesorId = UtilUUID.generarNuevoUUID();
        var versionRepositorioId = UtilUUID.generarNuevoUUID();
        artefacto = ArtefactoDomain.reconstruir(generacion.getArtefacto(), UtilUUID.generarNuevoUUID(),
                versionRepositorioId, asesorId);
        asesor = AsesorDomain.reconstruir(asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co",
                Instant.now(), null);
        versionRepositorio = VersionRepositorioArtefactoDomain.reconstruir(versionRepositorioId, "Formato A", 1,
                UtilUUID.generarNuevoUUID(), "Repo", new DatosArchivo("a.pdf", TIPO, 10L));
        carga = CargaPendienteDomain.reconstruir(generacion.getCarga(), generacion.getArtefacto(),
                generacion.getSolicitante(), "informe.pdf", TIPO, 2048L, ETAG);
        analisis = AnalisisArchivoDomain.reconstruir(TIPO, ETAG);
        revisionVigente = RevisionAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.EN_PROGRESO, Instant.now());
    }

    private void prepararFindersDeCarga(int ultimoNumero) {
        when(artefactoBloqueadoPorIdFinder.obtener(generacion.getArtefacto())).thenReturn(artefacto);
        when(estudianteArtefactoExisteFinder.obtener(
                new ParticipacionEstudianteArtefacto(generacion.getSolicitante(), generacion.getArtefacto())))
                .thenReturn(true);
        when(asesorPorIdFinder.obtener(artefacto.getAsesor())).thenReturn(asesor);
        when(versionRepositorioArtefactoPorIdFinder.obtener(artefacto.getVersionRepositorioArtefacto()))
                .thenReturn(versionRepositorio);
        when(cargaPendienteFinder.obtener(generacion.getCarga())).thenReturn(carga);
        when(tamanoMaximoArchivoFinder.obtener()).thenReturn(MAXIMO);
        when(ultimoNumeroVersionArtefactoFinder.obtener(generacion.getArtefacto())).thenReturn(ultimoNumero);
        when(revisionVigenteUltimaVersionFinder.obtener(generacion.getArtefacto())).thenReturn(revisionVigente);
    }

    private void prepararFinders(int ultimoNumero) {
        prepararFindersDeCarga(ultimoNumero);
        when(analisisCargaFinder.obtener(generacion.getCarga())).thenReturn(analisis);
    }

    @Test
    void debeRegistrarYPublicar_conOrden() {
        // Arrange
        prepararFinders(4);

        // Act
        var resultado = useCase.ejecutar(generacion);

        // Assert
        var ordenFinders = inOrder(artefactoBloqueadoPorIdFinder, estudianteArtefactoExisteFinder,
                asesorPorIdFinder, versionRepositorioArtefactoPorIdFinder, cargaPendienteFinder,
                tamanoMaximoArchivoFinder, ultimoNumeroVersionArtefactoFinder, revisionVigenteUltimaVersionFinder,
                validator, analisisCargaFinder, contenidoValidator);
        ordenFinders.verify(artefactoBloqueadoPorIdFinder).obtener(generacion.getArtefacto());
        ordenFinders.verify(estudianteArtefactoExisteFinder).obtener(any());
        ordenFinders.verify(asesorPorIdFinder).obtener(artefacto.getAsesor());
        ordenFinders.verify(versionRepositorioArtefactoPorIdFinder).obtener(artefacto.getVersionRepositorioArtefacto());
        ordenFinders.verify(cargaPendienteFinder).obtener(generacion.getCarga());
        ordenFinders.verify(tamanoMaximoArchivoFinder).obtener();
        ordenFinders.verify(ultimoNumeroVersionArtefactoFinder).obtener(generacion.getArtefacto());
        ordenFinders.verify(revisionVigenteUltimaVersionFinder).obtener(generacion.getArtefacto());
        var captorPrevias = ArgumentCaptor.forClass(PrecondicionesGeneracionVersion.class);
        ordenFinders.verify(validator).validar(eq(generacion), captorPrevias.capture());
        ordenFinders.verify(analisisCargaFinder).obtener(generacion.getCarga());
        ordenFinders.verify(contenidoValidator).validar(generacion, new CargaAnalizada(carga, analisis));
        var previas = captorPrevias.getValue();
        assertThat(previas.artefacto()).isSameAs(artefacto);
        assertThat(previas.carga()).isSameAs(carga);
        assertThat(previas.tamanoMaximoBytes()).isEqualTo(MAXIMO);
        assertThat(previas.versionAnterior()).isEqualTo(new RevisionVersionAnterior(4, revisionVigente));
        verify(artefactoBloqueadoPorIdFinder, times(1)).obtener(any());
        verify(estudianteArtefactoExisteFinder, times(1)).obtener(any());
        verify(asesorPorIdFinder, times(1)).obtener(any());
        verify(versionRepositorioArtefactoPorIdFinder, times(1)).obtener(any());
        verify(cargaPendienteFinder, times(1)).obtener(any());
        verify(analisisCargaFinder, times(1)).obtener(any());
        verify(ultimoNumeroVersionArtefactoFinder, times(1)).obtener(any());
        verify(revisionVigenteUltimaVersionFinder, times(1)).obtener(any());
        verify(tamanoMaximoArchivoFinder, times(1)).obtener();
        var captorVersion = ArgumentCaptor.forClass(VersionArtefactoEntity.class);
        var captorUri = ArgumentCaptor.forClass(String.class);
        var captorEvento = ArgumentCaptor.forClass(DomainEvent.class);
        var orden = inOrder(validator, almacenamiento, versionOutputPort, eventPublisher);
        orden.verify(almacenamiento).copiarCargaAUri(eq(generacion.getCarga()), eq(ETAG), captorUri.capture());
        orden.verify(versionOutputPort).registrar(captorVersion.capture());
        orden.verify(eventPublisher).publish(captorEvento.capture());
        orden.verify(almacenamiento).eliminarCarga(generacion.getCarga());
        var entity = captorVersion.getValue();
        assertThat(entity.numeroVersion()).isEqualTo(5);
        assertThat(entity.artefacto()).isEqualTo(generacion.getArtefacto());
        assertThat(entity.uriArchivo()).isEqualTo(captorUri.getValue()).startsWith("versiones/");
        var evento = (VersionArtefactoGeneradaEvent) captorEvento.getValue();
        assertThat(evento.getNombreDocumento()).isEqualTo("Formato A");
        assertThat(evento.getAsesor().nombre()).isEqualTo("Ana Perez");
        assertThat(evento.getAsesor().email()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getNumeroVersion()).isEqualTo(5);
        assertThat(resultado.id()).isEqualTo(entity.id());
        assertThat(resultado.numeroVersion()).isEqualTo(5);
    }

    @Test
    void debeNoAnalizarElArchivo_cuandoLasPrecondicionesSeRechazan() {
        // Arrange
        prepararFindersDeCarga(4);
        doThrow(new CargaNoCorrespondeException(generacion.getCarga(), "plantilla.docx"))
                .when(validator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(generacion)).isInstanceOf(CargaNoCorrespondeException.class);
        verifyNoInteractions(analisisCargaFinder, almacenamiento, versionOutputPort, eventPublisher);
        verifyNoInteractions(contenidoValidator);
    }

    @Test
    void debeNoTocarNada_cuandoElContenidoSeRechaza() {
        // Arrange
        prepararFindersDeCarga(4);
        when(analisisCargaFinder.obtener(generacion.getCarga())).thenReturn(analisis);
        doThrow(new CargaModificadaException(generacion.getCarga(), "plantilla.docx"))
                .when(contenidoValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(generacion)).isInstanceOf(CargaModificadaException.class);
        verifyNoInteractions(almacenamiento, versionOutputPort, eventPublisher);
        verify(logger, never()).info(eq(VersionArtefactoKey.LOG_GENERADA), any(), any(), any());
    }
}
