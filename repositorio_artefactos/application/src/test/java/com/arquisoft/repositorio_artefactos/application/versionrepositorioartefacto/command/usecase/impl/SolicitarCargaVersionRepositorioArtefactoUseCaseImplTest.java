package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.RepositorioArtefactoPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.SolicitudCargaEntity;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.model.FormularioCarga;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.SolicitarCargaVersionRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitarCargaVersionRepositorioArtefactoUseCaseImplTest {

    private static final long MAXIMO = 5_242_880L;

    @Mock
    private AdministradorPorIdFinder administradorPorIdFinder;
    @Mock
    private RepositorioArtefactoPorIdFinder repositorioArtefactoPorIdFinder;
    @Mock
    private TamanoMaximoArchivoFinder tamanoMaximoArchivoFinder;
    @Mock
    private SolicitarCargaVersionRepositorioArtefactoValidator validator;
    @Mock
    private AlmacenamientoVersionOutputPort almacenamiento;
    @Mock
    private AppLogger logger;

    private SolicitarCargaVersionRepositorioArtefactoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new SolicitarCargaVersionRepositorioArtefactoUseCaseImpl(administradorPorIdFinder,
                repositorioArtefactoPorIdFinder, tamanoMaximoArchivoFinder, validator, almacenamiento, logger);
    }

    private SolicitudCargaVersionRepositorioArtefactoDomain solicitud() {
        return SolicitudCargaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "formato.pdf", "application/pdf", 2048L);
    }

    @Test
    void debeGenerarFormulario_cuandoValido() {
        // Arrange
        var solicitud = solicitud();
        var administrador = AdministradorDomain.crear(
                solicitud.getSolicitante(), "123", "Ana", "ana@uco.edu.co", Instant.now());
        var repositorio = RepositorioArtefactoDomain.reconstruir(
                solicitud.getRepositorio(), "Repo", "Desc", UtilUUID.generarNuevoUUID());
        var expiraEn = Instant.now().plusSeconds(900);
        when(administradorPorIdFinder.obtener(solicitud.getSolicitante())).thenReturn(administrador);
        when(repositorioArtefactoPorIdFinder.obtener(solicitud.getRepositorio())).thenReturn(repositorio);
        when(tamanoMaximoArchivoFinder.obtener()).thenReturn(MAXIMO);
        when(almacenamiento.generarFormularioCarga(any()))
                .thenReturn(new FormularioCarga("http://minio/bucket", Map.of("key", "valor"), expiraEn));

        // Act
        var resultado = useCase.ejecutar(solicitud);

        // Assert
        var orden = inOrder(administradorPorIdFinder, repositorioArtefactoPorIdFinder, tamanoMaximoArchivoFinder,
                validator, almacenamiento);
        orden.verify(administradorPorIdFinder).obtener(solicitud.getSolicitante());
        orden.verify(repositorioArtefactoPorIdFinder).obtener(solicitud.getRepositorio());
        orden.verify(tamanoMaximoArchivoFinder).obtener();
        orden.verify(validator).validar(solicitud, administrador, repositorio, MAXIMO);
        orden.verify(almacenamiento).generarFormularioCarga(any(SolicitudCargaEntity.class));
        assertThat(resultado.carga()).isEqualTo(solicitud.getCarga());
        assertThat(resultado.url()).isEqualTo("http://minio/bucket");
        assertThat(resultado.campos()).containsEntry("key", "valor");
        assertThat(resultado.expiraEn()).isEqualTo(expiraEn);
        var captor = ArgumentCaptor.forClass(SolicitudCargaEntity.class);
        verify(almacenamiento).generarFormularioCarga(captor.capture());
        assertThat(captor.getValue().nombreArchivo()).isEqualTo("formato.pdf");
        assertThat(captor.getValue().tamanoBytes()).isEqualTo(2048L);
        verify(logger).info(any(ClaveMensaje.class), eq(solicitud.getRepositorio()), eq(solicitud.getSolicitante()));
    }

    @Test
    void debeNoGenerar_cuandoValidatorRechaza() {
        // Arrange
        var solicitud = solicitud();
        when(administradorPorIdFinder.obtener(any())).thenReturn(AdministradorDomain.VACIO);
        when(repositorioArtefactoPorIdFinder.obtener(any())).thenReturn(RepositorioArtefactoDomain.VACIO);
        when(tamanoMaximoArchivoFinder.obtener()).thenReturn(MAXIMO);
        doThrow(new AdministradorNoVigenteException(solicitud.getSolicitante()))
                .when(validator).validar(any(), any(), any(), eq(MAXIMO));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(solicitud)).isInstanceOf(AdministradorNoVigenteException.class);
        verifyNoInteractions(almacenamiento);
        verify(logger, never()).info(eq(VersionRepositorioArtefactoKey.LOG_CARGA_SOLICITADA), any(), any());
    }
}
