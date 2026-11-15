package com.arquisoft.biblioteca.application.documentacion.query.finder.impl;

import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.ArchivoDocumentacionAccesoQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.entity.ArchivoDocumentacionAccesoEntity;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArchivoDocumentacionQueryFinderImplTest {

    @Mock
    private ArchivoDocumentacionAccesoQueryOutputPort archivoDocumentacionAccesoQueryOutputPort;

    @InjectMocks
    private ArchivoDocumentacionQueryFinderImpl finder;

    @Test
    void debeDevolverElArchivoConSuEstado_cuandoExiste() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(archivoDocumentacionAccesoQueryOutputPort.obtenerPorId(documentacion)).thenReturn(Optional.of(
                new ArchivoDocumentacionAccesoEntity(documentacion, "PUBLICADO", "artefactos", "p/a.zip", "a.zip")));

        // Act
        var archivo = finder.obtener(documentacion);

        // Assert
        assertThat(archivo.documentacion()).isEqualTo(documentacion);
        assertThat(archivo.estado()).isEqualTo(EstadoDocumentacion.PUBLICADO);
        assertThat(archivo.bucket()).isEqualTo("artefactos");
        assertThat(archivo.claveObjeto()).isEqualTo("p/a.zip");
        assertThat(archivo.nombreArchivo()).isEqualTo("a.zip");
        verify(archivoDocumentacionAccesoQueryOutputPort).obtenerPorId(documentacion);
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(archivoDocumentacionAccesoQueryOutputPort.obtenerPorId(documentacion)).thenReturn(Optional.empty());

        // Act
        var archivo = finder.obtener(documentacion);

        // Assert
        assertThat(archivo).isSameAs(ArchivoDocumentacion.VACIO);
    }

    @Test
    void debeNormalizarNulosATextoVacio_cuandoLaFilaNoTieneUbicacion() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(archivoDocumentacionAccesoQueryOutputPort.obtenerPorId(documentacion)).thenReturn(Optional.of(
                new ArchivoDocumentacionAccesoEntity(documentacion, "PENDIENTE", null, null, null)));

        // Act
        var archivo = finder.obtener(documentacion);

        // Assert
        assertThat(archivo.esVacio()).isFalse();
        assertThat(archivo.bucket()).isEqualTo(UtilTexto.VACIO);
        assertThat(archivo.claveObjeto()).isEqualTo(UtilTexto.VACIO);
        assertThat(archivo.nombreArchivo()).isEqualTo(UtilTexto.VACIO);
    }
}
