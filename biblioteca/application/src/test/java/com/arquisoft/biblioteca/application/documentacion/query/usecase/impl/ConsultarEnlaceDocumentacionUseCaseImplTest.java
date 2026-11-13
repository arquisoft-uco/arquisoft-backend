package com.arquisoft.biblioteca.application.documentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.finder.EstadoDeDocumentacionQueryFinder;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.DocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.EnlaceDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.validator.ConsultarEnlaceDocumentacionValidator;
import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoCompartibleException;
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
    private EstadoDeDocumentacionQueryFinder estadoDeDocumentacionQueryFinder;

    @Mock
    private ConsultarEnlaceDocumentacionValidator consultarEnlaceDocumentacionValidator;

    @Mock
    private DocumentacionQueryOutputPort documentacionQueryOutputPort;

    @Mock
    private EnlaceDocumentacionQueryOutputPort enlaceDocumentacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEnlaceDocumentacionUseCaseImpl useCase;

    @Test
    void debeDevolverEnlaceYDatosSinUrlDelArchivo_cuandoLaDocumentacionEstaPublicada() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var criteria = new EnlaceDocumentacionCriteria(id);
        var entregable = new DocumentacionReadModel.EntregableProyectoGradoResumen(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, "Aprobado");
        var bibliotecario = new DocumentacionReadModel.BibliotecarioResumen(UtilUUID.generarNuevoUUID(), "Ana");
        var estado = new DocumentacionReadModel.EstadoDocumentacionResumen("PUBLICADO", "Publicado");
        var leida = new DocumentacionReadModel(id, "https://archivo.test/secreto",
                Instant.parse("2026-11-10T10:00:00Z"), entregable, bibliotecario, estado);
        when(estadoDeDocumentacionQueryFinder.obtener(id)).thenReturn(EstadoDocumentacion.PUBLICADO);
        when(documentacionQueryOutputPort.consultarPorId(id)).thenReturn(leida);
        when(enlaceDocumentacionQueryOutputPort.generar(id)).thenReturn("https://front.test/doc/" + id);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.enlace()).isEqualTo("https://front.test/doc/" + id);
        assertThat(resultado.id()).isEqualTo(id);
        assertThat(resultado.entregableProyectoGrado()).isSameAs(entregable);
        assertThat(resultado.bibliotecario()).isSameAs(bibliotecario);
        assertThat(resultado.estadoDocumentacion()).isSameAs(estado);
        assertThat(resultado.toString()).doesNotContain("archivo.test");
        var orden = inOrder(estadoDeDocumentacionQueryFinder, consultarEnlaceDocumentacionValidator,
                documentacionQueryOutputPort, enlaceDocumentacionQueryOutputPort);
        orden.verify(estadoDeDocumentacionQueryFinder, times(1)).obtener(id);
        orden.verify(consultarEnlaceDocumentacionValidator).validar(criteria, EstadoDocumentacion.PUBLICADO);
        orden.verify(documentacionQueryOutputPort, times(1)).consultarPorId(id);
        orden.verify(enlaceDocumentacionQueryOutputPort, times(1)).generar(id);
        verify(logger).debug(eq(DocumentacionKey.LOG_VERIFICACION_ENLACE), eq(id), eq("PUBLICADO"));
        verify(logger).debug(eq(DocumentacionKey.LOG_ENLACE_GENERADO), eq(id));
    }

    @Test
    void debeNoLeerNiGenerarEnlace_cuandoElValidatorRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var criteria = new EnlaceDocumentacionCriteria(id);
        when(estadoDeDocumentacionQueryFinder.obtener(id)).thenReturn(EstadoDocumentacion.VACIO);
        doThrow(new DocumentacionNoCompartibleException(id))
                .when(consultarEnlaceDocumentacionValidator).validar(criteria, EstadoDocumentacion.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(DocumentacionNoCompartibleException.class);
        verify(documentacionQueryOutputPort, never()).consultarPorId(any());
        verify(enlaceDocumentacionQueryOutputPort, never()).generar(any());
        verify(logger, never()).debug(eq(DocumentacionKey.LOG_ENLACE_GENERADO), eq(id));
    }
}
