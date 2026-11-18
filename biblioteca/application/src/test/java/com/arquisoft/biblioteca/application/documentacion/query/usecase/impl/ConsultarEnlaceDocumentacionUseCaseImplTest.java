package com.arquisoft.biblioteca.application.documentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.finder.ArchivoDocumentacionQueryFinder;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.EnlaceDescargaDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.validator.ConsultarEnlaceDocumentacionValidator;
import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoCompartibleException;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
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
class ConsultarEnlaceDocumentacionUseCaseImplTest {

    @Mock
    private ArchivoDocumentacionQueryFinder archivoDocumentacionQueryFinder;

    @Mock
    private ConsultarEnlaceDocumentacionValidator consultarEnlaceDocumentacionValidator;

    @Mock
    private EnlaceDescargaDocumentacionQueryOutputPort enlaceDescargaDocumentacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEnlaceDocumentacionUseCaseImpl useCase;

    @Test
    void debeDevolverElEnlaceDeDescarga_cuandoLaDocumentacionEstaPublicada() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new EnlaceDocumentacionCriteria(documentacion);
        var archivo = new ArchivoDocumentacion(documentacion, EstadoDocumentacion.PUBLICADO,
                "entregables", "paquetes/a.zip", "a.zip");
        var esperado = new EnlaceDescargaDocumentacionReadModel("https://minio.test/a", Instant.now());
        when(archivoDocumentacionQueryFinder.obtener(documentacion)).thenReturn(archivo);
        when(enlaceDescargaDocumentacionQueryOutputPort.generar(documentacion, "entregables", "paquetes/a.zip",
                "a.zip")).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(archivoDocumentacionQueryFinder, consultarEnlaceDocumentacionValidator,
                enlaceDescargaDocumentacionQueryOutputPort);
        orden.verify(archivoDocumentacionQueryFinder).obtener(documentacion);
        orden.verify(consultarEnlaceDocumentacionValidator).validar(criteria, archivo);
        orden.verify(enlaceDescargaDocumentacionQueryOutputPort).generar(documentacion, "entregables",
                "paquetes/a.zip", "a.zip");
        verify(archivoDocumentacionQueryFinder, times(1)).obtener(documentacion);
        verify(logger).debug(eq(DocumentacionKey.LOG_CONSULTANDO_ENLACE), eq(documentacion));
        verify(logger).debug(eq(DocumentacionKey.LOG_VERIFICACION_ENLACE), eq(true), eq("PUBLICADO"));
        verify(logger).debug(eq(DocumentacionKey.LOG_ENLACE_GENERADO), eq(documentacion), eq(esperado.expiraEn()));
    }

    @Test
    void debeNoGenerarEnlace_cuandoElValidatorRechaza() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new EnlaceDocumentacionCriteria(documentacion);
        when(archivoDocumentacionQueryFinder.obtener(documentacion)).thenReturn(ArchivoDocumentacion.VACIO);
        doThrow(new DocumentacionNoCompartibleException(documentacion))
                .when(consultarEnlaceDocumentacionValidator).validar(criteria, ArchivoDocumentacion.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(DocumentacionNoCompartibleException.class);
        verify(enlaceDescargaDocumentacionQueryOutputPort, never()).generar(any(), any(), any(), any());
        verify(logger, never()).debug(eq(DocumentacionKey.LOG_ENLACE_GENERADO), eq(documentacion), any());
    }
}
