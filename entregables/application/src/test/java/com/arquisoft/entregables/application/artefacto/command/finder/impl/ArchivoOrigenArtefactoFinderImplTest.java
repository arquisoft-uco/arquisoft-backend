package com.arquisoft.entregables.application.artefacto.command.finder.impl;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.AlmacenamientoArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArchivoOrigenArtefactoEntity;
import com.arquisoft.shared.util.UtilTexto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArchivoOrigenArtefactoFinderImplTest {

    @Mock
    private AlmacenamientoArtefactoOutputPort almacenamientoArtefactoOutputPort;

    @InjectMocks
    private ArchivoOrigenArtefactoFinderImpl finder;

    @Test
    void debeDevolverElEtag_cuandoElArchivoExiste() {
        // Arrange
        when(almacenamientoArtefactoOutputPort.obtenerMetadatosOrigen("clave"))
                .thenReturn(Optional.of(new ArchivoOrigenArtefactoEntity("clave", "etag-9")));

        // Act
        var etag = finder.obtener("clave");

        // Assert
        assertThat(etag).isEqualTo("etag-9");
    }

    @Test
    void debeDevolverTextoVacio_cuandoElArchivoNoExiste() {
        // Arrange
        when(almacenamientoArtefactoOutputPort.obtenerMetadatosOrigen("clave")).thenReturn(Optional.empty());

        // Act
        var etag = finder.obtener("clave");

        // Assert
        assertThat(etag).isEqualTo(UtilTexto.VACIO);
    }
}
