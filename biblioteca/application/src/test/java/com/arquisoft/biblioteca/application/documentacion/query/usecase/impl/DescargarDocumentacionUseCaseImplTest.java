package com.arquisoft.biblioteca.application.documentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DescargaDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.finder.ArchivoDocumentacionQueryFinder;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.EnlaceDescargaDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.validator.DescargarDocumentacionValidator;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescargarDocumentacionUseCaseImplTest {

    @Mock
    private ArchivoDocumentacionQueryFinder archivoDocumentacionQueryFinder;

    @Mock
    private DescargarDocumentacionValidator descargarDocumentacionValidator;

    @Mock
    private EnlaceDescargaDocumentacionQueryOutputPort enlaceDescargaDocumentacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private DescargarDocumentacionUseCaseImpl useCase;

    @Test
    void debeDevolverElEnlace_cuandoLaValidacionPasa() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new DescargaDocumentacionCriteria(documentacion, false);
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
        var orden = inOrder(archivoDocumentacionQueryFinder, descargarDocumentacionValidator,
                enlaceDescargaDocumentacionQueryOutputPort);
        orden.verify(archivoDocumentacionQueryFinder).obtener(documentacion);
        orden.verify(descargarDocumentacionValidator).validar(criteria, archivo);
        orden.verify(enlaceDescargaDocumentacionQueryOutputPort).generar(documentacion, "entregables",
                "paquetes/a.zip", "a.zip");
        verify(archivoDocumentacionQueryFinder, times(1)).obtener(documentacion);
        verify(logger).debug(eq(DocumentacionKey.LOG_DESCARGANDO), eq(documentacion), eq(false));
        verify(logger).debug(eq(DocumentacionKey.LOG_VERIFICACION_DESCARGA), eq(true), eq("PUBLICADO"));
        verify(logger).debug(eq(DocumentacionKey.LOG_DESCARGA_GENERADA), eq(documentacion), eq(esperado.expiraEn()));
    }
}
