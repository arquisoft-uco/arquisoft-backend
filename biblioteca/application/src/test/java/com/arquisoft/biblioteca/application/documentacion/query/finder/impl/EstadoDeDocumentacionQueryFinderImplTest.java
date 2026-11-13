package com.arquisoft.biblioteca.application.documentacion.query.finder.impl;

import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.DocumentacionAccesoQueryOutputPort;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoDeDocumentacionQueryFinderImplTest {

    @Mock
    private DocumentacionAccesoQueryOutputPort documentacionAccesoQueryOutputPort;

    @InjectMocks
    private EstadoDeDocumentacionQueryFinderImpl finder;

    @Test
    void debeDevolverEstado_cuandoLaDocumentacionExiste() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(documentacionAccesoQueryOutputPort.obtenerEstadoPorId(documentacion))
                .thenReturn(Optional.of("PUBLICADO"));

        // Act
        var estado = finder.obtener(documentacion);

        // Assert
        assertThat(estado).isEqualTo(EstadoDocumentacion.PUBLICADO);
        verify(documentacionAccesoQueryOutputPort, times(1)).obtenerEstadoPorId(documentacion);
    }

    @Test
    void debeDevolverVacio_cuandoLaDocumentacionNoExiste() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(documentacionAccesoQueryOutputPort.obtenerEstadoPorId(documentacion)).thenReturn(Optional.empty());

        // Act
        var estado = finder.obtener(documentacion);

        // Assert
        assertThat(estado).isEqualTo(EstadoDocumentacion.VACIO);
    }
}
