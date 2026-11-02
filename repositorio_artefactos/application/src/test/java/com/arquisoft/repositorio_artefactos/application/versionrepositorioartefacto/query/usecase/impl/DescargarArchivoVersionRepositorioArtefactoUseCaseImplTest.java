package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.ArchivoVersionRepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.finder.ArchivoVersionQueryFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.EnlaceDescargaVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.EnlaceDescargaVersionQueryOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.validator.DescargarArchivoVersionRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescargarArchivoVersionRepositorioArtefactoUseCaseImplTest {

    @Mock
    private ArchivoVersionQueryFinder archivoVersionQueryFinder;

    @Mock
    private DescargarArchivoVersionRepositorioArtefactoValidator validator;

    @Mock
    private EnlaceDescargaVersionQueryOutputPort enlaceOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private DescargarArchivoVersionRepositorioArtefactoUseCaseImpl useCase;

    @Test
    void debeGenerarEnlace_cuandoLaVersionExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var archivo = ArchivoVersionDomain.reconstruir("versiones/" + version, "formato.pdf");
        var expiraEn = Instant.parse("2026-11-02T10:05:00Z");
        var enlace = new EnlaceDescargaVersionReadModel("https://s3.test/firmada", expiraEn);
        when(archivoVersionQueryFinder.obtener(version)).thenReturn(archivo);
        when(enlaceOutputPort.generar(version, archivo.getUriArchivo(), archivo.getNombreArchivo()))
                .thenReturn(enlace);

        // Act
        var resultado = useCase.ejecutar(new ArchivoVersionRepositorioArtefactoCriteria(version));

        // Assert
        assertThat(resultado).isSameAs(enlace);
        verify(archivoVersionQueryFinder, times(1)).obtener(version);
        var orden = inOrder(logger, archivoVersionQueryFinder, validator, enlaceOutputPort);
        orden.verify(logger).debug(VersionRepositorioArtefactoKey.LOG_DESCARGANDO, version);
        orden.verify(archivoVersionQueryFinder).obtener(version);
        orden.verify(validator).validar(any(ExistenciaVersionRepositorioArtefacto.class));
        orden.verify(enlaceOutputPort).generar(version, archivo.getUriArchivo(), archivo.getNombreArchivo());
        orden.verify(logger).debug(VersionRepositorioArtefactoKey.LOG_DESCARGA_GENERADA, version, expiraEn);
    }

    @Test
    void debeNoGenerarEnlace_cuandoElValidatorRechaza() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        when(archivoVersionQueryFinder.obtener(version)).thenReturn(ArchivoVersionDomain.VACIO);
        doThrow(new VersionRepositorioArtefactoNoEncontradaException(version))
                .when(validator).validar(any(ExistenciaVersionRepositorioArtefacto.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(new ArchivoVersionRepositorioArtefactoCriteria(version)))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
        verify(enlaceOutputPort, never()).generar(any(), anyString(), anyString());
        verify(logger, never()).debug(eq(VersionRepositorioArtefactoKey.LOG_DESCARGA_GENERADA), any(), any());
    }
}
