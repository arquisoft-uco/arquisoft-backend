package com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.minio;

import com.arquisoft.biblioteca.infrastructure.config.BibliotecaAlmacenamientoProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AlmacenamientoDocumentacionMinioOutputAdapterTest {

    @Test
    void debeDevolverBucketEntregables_cuandoSeConsulta() {
        // Arrange
        var propiedades = new BibliotecaAlmacenamientoProperties();
        propiedades.setBucketEntregables("entregables");
        var adapter = new AlmacenamientoDocumentacionMinioOutputAdapter(propiedades);

        // Act
        var bucket = adapter.bucketEntregables();

        // Assert
        assertThat(bucket).isEqualTo("entregables");
    }
}
