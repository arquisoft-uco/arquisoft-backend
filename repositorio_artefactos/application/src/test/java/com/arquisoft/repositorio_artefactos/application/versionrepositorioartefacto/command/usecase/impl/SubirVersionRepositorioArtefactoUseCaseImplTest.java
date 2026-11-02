package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.RepositorioArtefactoBloqueadoPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.AnalisisCargaFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.CargaPendienteFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.UltimaVersionDocumentoFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.SubirVersionRepositorioArtefactoContenidoValidator;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.SubirVersionRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.UltimaVersionDocumento;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.event.VersionRepositorioArtefactoSubidaEvent;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubirVersionRepositorioArtefactoUseCaseImplTest {

    private static final long MAXIMO = 5_242_880L;
    private static final String TIPO = "application/pdf";
    private static final String HASH = "a".repeat(64);
    private static final String ETAG = "etag-1";

    @Mock
    private AdministradorPorIdFinder administradorPorIdFinder;
    @Mock
    private RepositorioArtefactoBloqueadoPorIdFinder repositorioArtefactoBloqueadoPorIdFinder;
    @Mock
    private CargaPendienteFinder cargaPendienteFinder;
    @Mock
    private AnalisisCargaFinder analisisCargaFinder;
    @Mock
    private UltimaVersionDocumentoFinder ultimaVersionDocumentoFinder;
    @Mock
    private TamanoMaximoArchivoFinder tamanoMaximoArchivoFinder;
    @Mock
    private SubirVersionRepositorioArtefactoValidator validator;
    @Mock
    private SubirVersionRepositorioArtefactoContenidoValidator contenidoValidator;
    @Mock
    private AlmacenamientoVersionOutputPort almacenamiento;
    @Mock
    private VersionRepositorioArtefactoOutputPort versionOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private SubirVersionRepositorioArtefactoUseCaseImpl useCase;

    private SubidaVersionRepositorioArtefactoDomain subida;
    private AdministradorDomain administrador;
    private RepositorioArtefactoDomain repositorio;
    private CargaPendienteDomain carga;
    private AnalisisArchivoDomain analisis;

    @BeforeEach
    void setUp() {
        useCase = new SubirVersionRepositorioArtefactoUseCaseImpl(administradorPorIdFinder,
                repositorioArtefactoBloqueadoPorIdFinder, cargaPendienteFinder, analisisCargaFinder,
                ultimaVersionDocumentoFinder, tamanoMaximoArchivoFinder, validator, contenidoValidator,
                almacenamiento, versionOutputPort, eventPublisher, logger);
        subida = SubidaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Formato A", false);
        administrador = AdministradorDomain.crear(
                subida.getSolicitante(), "123", "Ana", "ana@uco.edu.co", Instant.now());
        repositorio = RepositorioArtefactoDomain.reconstruir(
                subida.getRepositorio(), "Repo Uno", "Desc", UtilUUID.generarNuevoUUID());
        carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "formato.pdf", TIPO, 2048L, ETAG);
        analisis = AnalisisArchivoDomain.reconstruir(TIPO, HASH, ETAG);
    }

    private void prepararFindersDeCarga(int ultimoNumero) {
        when(administradorPorIdFinder.obtener(subida.getSolicitante())).thenReturn(administrador);
        when(repositorioArtefactoBloqueadoPorIdFinder.obtener(subida.getRepositorio())).thenReturn(repositorio);
        when(cargaPendienteFinder.obtener(subida.getCarga())).thenReturn(carga);
        when(ultimaVersionDocumentoFinder.obtener(subida)).thenReturn(ultimoNumero == 0
                ? UltimaVersionDocumento.VACIO : new UltimaVersionDocumento(subida.getNombreDocumento(), ultimoNumero));
        when(tamanoMaximoArchivoFinder.obtener()).thenReturn(MAXIMO);
    }

    private void prepararFinders(int ultimoNumero) {
        prepararFindersDeCarga(ultimoNumero);
        when(analisisCargaFinder.obtener(subida.getCarga())).thenReturn(analisis);
    }

    @Test
    void debeCopiarRegistrarPublicarYBorrar_enEseOrden() {
        // Arrange
        prepararFinders(4);

        // Act
        var resultado = useCase.ejecutar(subida);

        // Assert
        var captorVersion = ArgumentCaptor.forClass(VersionRepositorioArtefactoEntity.class);
        var ordenFinders = inOrder(administradorPorIdFinder, repositorioArtefactoBloqueadoPorIdFinder,
                cargaPendienteFinder, ultimaVersionDocumentoFinder, tamanoMaximoArchivoFinder, validator,
                analisisCargaFinder, contenidoValidator);
        ordenFinders.verify(administradorPorIdFinder).obtener(subida.getSolicitante());
        ordenFinders.verify(repositorioArtefactoBloqueadoPorIdFinder).obtener(subida.getRepositorio());
        ordenFinders.verify(cargaPendienteFinder).obtener(subida.getCarga());
        ordenFinders.verify(ultimaVersionDocumentoFinder).obtener(subida);
        ordenFinders.verify(tamanoMaximoArchivoFinder).obtener();
        ordenFinders.verify(validator).validar(subida, administrador, repositorio, carga, MAXIMO,
                new UltimaVersionDocumento(subida.getNombreDocumento(), 4));
        ordenFinders.verify(analisisCargaFinder).obtener(subida.getCarga());
        ordenFinders.verify(contenidoValidator).validar(subida, carga, analisis);
        verify(administradorPorIdFinder, times(1)).obtener(any());
        verify(repositorioArtefactoBloqueadoPorIdFinder, times(1)).obtener(any());
        verify(cargaPendienteFinder, times(1)).obtener(any());
        verify(analisisCargaFinder, times(1)).obtener(any());
        verify(ultimaVersionDocumentoFinder, times(1)).obtener(any());
        verify(tamanoMaximoArchivoFinder, times(1)).obtener();
        var orden = inOrder(validator, almacenamiento, versionOutputPort, eventPublisher);
        orden.verify(almacenamiento).copiarCargaAUri(eq(subida.getCarga()), eq(ETAG), any());
        orden.verify(versionOutputPort).registrar(captorVersion.capture());
        orden.verify(eventPublisher).publish(any(DomainEvent.class));
        orden.verify(almacenamiento).eliminarCarga(subida.getCarga());
        assertThat(resultado.id()).isEqualTo(captorVersion.getValue().id());
        assertThat(resultado.numeroVersion()).isEqualTo(5);
    }

    @Test
    void debeNoAnalizarElArchivo_cuandoLasPrecondicionesSeRechazan() {
        // Arrange
        prepararFindersDeCarga(0);
        doThrow(new TamanoArchivoExcedidoException("plantilla.docx", MAXIMO + 1, MAXIMO))
                .when(validator).validar(any(), any(), any(), any(), anyLong(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(subida)).isInstanceOf(TamanoArchivoExcedidoException.class);
        verifyNoInteractions(analisisCargaFinder, almacenamiento, versionOutputPort, eventPublisher);
        verifyNoInteractions(contenidoValidator);
    }

    @Test
    void debeNoCopiar_cuandoElContenidoSeRechaza() {
        // Arrange
        prepararFinders(0);
        doThrow(new TipoArchivoNoCoincideException("plantilla.docx", "a", "b"))
                .when(contenidoValidator).validar(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(subida)).isInstanceOf(TipoArchivoNoCoincideException.class);
        verifyNoInteractions(almacenamiento, versionOutputPort, eventPublisher);
        verify(logger, never()).info(eq(VersionRepositorioArtefactoKey.LOG_SUBIDA), any(), any(), any());
    }

    @Test
    void debeNoRegistrarNiPublicarNiBorrar_cuandoLaCopiaCondicionadaSeRechaza() {
        // Arrange
        prepararFinders(2);
        doThrow(new InfrastructureException("etag cambiado", "MINIO_COPIA_RECHAZADA", new IllegalStateException("412")))
                .when(almacenamiento).copiarCargaAUri(eq(subida.getCarga()), eq(ETAG), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(subida)).isInstanceOf(InfrastructureException.class);
        verifyNoInteractions(versionOutputPort, eventPublisher);
        verify(almacenamiento, never()).eliminarCarga(any());
        verify(logger, never()).info(eq(VersionRepositorioArtefactoKey.LOG_SUBIDA), any(), any(), any());
    }

    @Test
    void debePublicarEvento_conNombreDelRepositorioYMetadatosDelArchivo() {
        // Arrange
        prepararFinders(0);

        // Act
        useCase.ejecutar(subida);

        // Assert
        var captor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(captor.capture());
        var evento = (VersionRepositorioArtefactoSubidaEvent) captor.getValue();
        assertThat(evento.getNombreRepositorio()).isEqualTo("Repo Uno");
        assertThat(evento.getNombreArchivo()).isEqualTo("formato.pdf");
        assertThat(evento.getTipoContenido()).isEqualTo(TIPO);
        assertThat(evento.getTamanoBytes()).isEqualTo(2048L);
        assertThat(evento.getVersionDocumento()).isEqualTo(1);
    }

    @Test
    void debeCrearVersion_conUltimoNumeroDelFinderYHashDelAnalisis() {
        // Arrange
        prepararFinders(7);

        // Act
        useCase.ejecutar(subida);

        // Assert
        var captor = ArgumentCaptor.forClass(VersionRepositorioArtefactoEntity.class);
        verify(versionOutputPort).registrar(captor.capture());
        var entity = captor.getValue();
        assertThat(entity.numeroVersion()).isEqualTo(8);
        assertThat(entity.hashSha256()).isEqualTo(HASH);
        assertThat(entity.nombreDocumento()).isEqualTo("Formato A");
        assertThat(entity.uriArchivo()).isEqualTo("versiones/" + entity.id());
        assertThat(entity.tamanoBytes()).isEqualTo(2048L);
    }

    @Test
    void debeGuardarYPublicarConElNombreDelDocumentoExistente_cuandoLaSubidaDifiereEnMayusculas() {
        // Arrange
        prepararFinders(0);
        when(ultimaVersionDocumentoFinder.obtener(subida)).thenReturn(new UltimaVersionDocumento("FORMATO a", 3));

        // Act
        useCase.ejecutar(subida);

        // Assert
        var captorEntidad = ArgumentCaptor.forClass(VersionRepositorioArtefactoEntity.class);
        verify(versionOutputPort).registrar(captorEntidad.capture());
        assertThat(captorEntidad.getValue().nombreDocumento()).isEqualTo("FORMATO a");
        assertThat(captorEntidad.getValue().numeroVersion()).isEqualTo(4);
        var captorEvento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(captorEvento.capture());
        var evento = (VersionRepositorioArtefactoSubidaEvent) captorEvento.getValue();
        assertThat(evento.getNombreDocumento()).isEqualTo("FORMATO a");
    }

    @Test
    void debeCopiar_conElEtagDeLaCargaYLaUriDeLaVersion() {
        // Arrange
        prepararFinders(0);

        // Act
        useCase.ejecutar(subida);

        // Assert
        var captor = ArgumentCaptor.forClass(String.class);
        verify(almacenamiento).copiarCargaAUri(eq(subida.getCarga()), eq(ETAG), captor.capture());
        assertThat(captor.getValue()).startsWith("versiones/");
    }
}
