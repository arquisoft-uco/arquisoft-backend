package com.arquisoft.biblioteca.application.documentacion.command.finder.impl;

import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.DocumentacionOutputPort;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.entity.DocumentacionEntity;
import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentacionPorIdFinderImplTest {

    @Mock
    private DocumentacionOutputPort documentacionOutputPort;

    @InjectMocks
    private DocumentacionPorIdFinderImpl finder;

    @Test
    void debeRetornarDomainReconstruido_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-11-15T10:00:00Z");
        when(documentacionOutputPort.obtenerPorId(id)).thenReturn(Optional.of(new DocumentacionEntity(
                id, "https://minio.test/a.zip", fecha, entregable, bibliotecario, "NO_PUBLICADO")));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getUrl()).isEqualTo("https://minio.test/a.zip");
        assertThat(resultado.getFechaPublicacion()).isEqualTo(fecha);
        assertThat(resultado.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(resultado.getBibliotecario()).isEqualTo(bibliotecario);
        assertThat(resultado.getEstadoDocumentacion()).isEqualTo(EstadoDocumentacion.NO_PUBLICADO);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(documentacionOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(DocumentacionDomain.VACIO);
    }
}
