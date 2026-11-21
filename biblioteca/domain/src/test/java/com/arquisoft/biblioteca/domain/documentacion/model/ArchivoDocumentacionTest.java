package com.arquisoft.biblioteca.domain.documentacion.model;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArchivoDocumentacionTest {

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(ArchivoDocumentacion.VACIO.esVacio()).isTrue();
        assertThat(ArchivoDocumentacion.VACIO.estado()).isEqualTo(EstadoDocumentacion.VACIO);
        assertThat(UtilUUID.esPorDefecto(ArchivoDocumentacion.VACIO.documentacion())).isTrue();
    }

    @Test
    void debeNoSerVacio_cuandoSeConstruyeConDatos() {
        // Arrange
        var archivo = new ArchivoDocumentacion(UtilUUID.generarNuevoUUID(), EstadoDocumentacion.PUBLICADO,
                "paquetes/a.zip", "a.zip");

        // Act & Assert
        assertThat(archivo.esVacio()).isFalse();
    }
}
