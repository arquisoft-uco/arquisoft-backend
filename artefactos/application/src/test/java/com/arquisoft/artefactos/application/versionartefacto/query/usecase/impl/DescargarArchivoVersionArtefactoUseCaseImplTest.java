package com.arquisoft.artefactos.application.versionartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.DescargaVersionArtefactoCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.ArchivoVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.EstudianteParticipaVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.model.ParticipacionVersionArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.EnlaceDescargaVersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.EnlaceDescargaVersionArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.query.validator.DescargarArchivoVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.versionartefacto.ArchivoVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.ActorSinAccesoVersionArtefactoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescargarArchivoVersionArtefactoUseCaseImplTest {

    @Mock
    private ArchivoVersionArtefactoQueryFinder archivoVersionArtefactoQueryFinder;

    @Mock
    private EstudianteParticipaVersionArtefactoQueryFinder estudianteParticipaVersionArtefactoQueryFinder;

    @Mock
    private DescargarArchivoVersionArtefactoValidator descargarArchivoVersionArtefactoValidator;

    @Mock
    private EnlaceDescargaVersionArtefactoQueryOutputPort enlaceDescargaVersionArtefactoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private DescargarArchivoVersionArtefactoUseCaseImpl useCase;

    private final DescargaVersionArtefactoCriteria criteria = new DescargaVersionArtefactoCriteria(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true, true);

    @Test
    void debeGenerarEnlace_cuandoElActorTieneAcceso() {
        // Arrange
        var archivo = ArchivoVersionArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "versiones/x", 2, "Documento", "Proyecto", false);
        var participacion = new ParticipacionVersionArtefacto(criteria.versionArtefacto(), criteria.actor());
        var esperado = new EnlaceDescargaVersionArtefactoReadModel("http://minio/x", Instant.now());
        when(archivoVersionArtefactoQueryFinder.obtener(criteria.versionArtefacto())).thenReturn(archivo);
        when(estudianteParticipaVersionArtefactoQueryFinder.obtener(participacion)).thenReturn(true);
        when(enlaceDescargaVersionArtefactoQueryOutputPort.generar(
                criteria.versionArtefacto(), "versiones/x", "Documento-v2")).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(archivoVersionArtefactoQueryFinder, estudianteParticipaVersionArtefactoQueryFinder,
                descargarArchivoVersionArtefactoValidator, enlaceDescargaVersionArtefactoQueryOutputPort);
        orden.verify(archivoVersionArtefactoQueryFinder, times(1)).obtener(criteria.versionArtefacto());
        orden.verify(estudianteParticipaVersionArtefactoQueryFinder, times(1)).obtener(participacion);
        orden.verify(descargarArchivoVersionArtefactoValidator).validar(criteria, archivo, true);
        orden.verify(enlaceDescargaVersionArtefactoQueryOutputPort)
                .generar(criteria.versionArtefacto(), "versiones/x", "Documento-v2");
        verify(logger).debug(eq(VersionArtefactoKey.LOG_DESCARGA_GENERADA), eq(criteria.versionArtefacto()),
                eq(esperado.expiraEn()));
    }

    @Test
    void debeNoGenerarEnlace_cuandoElValidatorRechaza() {
        // Arrange
        when(archivoVersionArtefactoQueryFinder.obtener(criteria.versionArtefacto()))
                .thenReturn(ArchivoVersionArtefactoDomain.VACIO);
        when(estudianteParticipaVersionArtefactoQueryFinder.obtener(any())).thenReturn(false);
        doThrow(new ActorSinAccesoVersionArtefactoException(criteria.actor(), criteria.versionArtefacto()))
                .when(descargarArchivoVersionArtefactoValidator)
                .validar(criteria, ArchivoVersionArtefactoDomain.VACIO, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(ActorSinAccesoVersionArtefactoException.class);
        verify(enlaceDescargaVersionArtefactoQueryOutputPort, never()).generar(any(), any(), any());
        verify(logger, never()).debug(eq(VersionArtefactoKey.LOG_DESCARGA_GENERADA), any(), any());
    }
}
