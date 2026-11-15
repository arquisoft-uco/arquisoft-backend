package com.arquisoft.biblioteca.domain.documentacion;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentacionDomainTest {

    @Test
    void debeReconstruirConLosSeisAtributos_cuandoSeIndicanTodos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-11-15T10:00:00Z");

        // Act
        var documentacion = DocumentacionDomain.reconstruir(
                id, "https://minio.test/a.zip", fecha, entregable, bibliotecario, EstadoDocumentacion.PUBLICADO);

        // Assert
        assertThat(documentacion.esVacio()).isFalse();
        assertThat(documentacion.getId()).isEqualTo(id);
        assertThat(documentacion.getUrl()).isEqualTo("https://minio.test/a.zip");
        assertThat(documentacion.getFechaPublicacion()).isEqualTo(fecha);
        assertThat(documentacion.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(documentacion.getBibliotecario()).isEqualTo(bibliotecario);
        assertThat(documentacion.getEstadoDocumentacion()).isEqualTo(EstadoDocumentacion.PUBLICADO);
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(DocumentacionDomain.VACIO.esVacio()).isTrue();
        assertThat(DocumentacionDomain.VACIO.getEstadoDocumentacion()).isEqualTo(EstadoDocumentacion.VACIO);
        assertThat(UtilUUID.esPorDefecto(DocumentacionDomain.VACIO.getId())).isTrue();
    }
}
