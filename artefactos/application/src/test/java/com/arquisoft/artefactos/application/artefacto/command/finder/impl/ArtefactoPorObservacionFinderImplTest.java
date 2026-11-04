package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtefactoPorObservacionFinderImplTest {

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @InjectMocks
    private ArtefactoPorObservacionFinderImpl finder;

    @Test
    void debeDevolverElArtefacto_cuandoLaObservacionExiste() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerPorObservacion(observacion)).thenReturn(Optional.of(
                new ArtefactoEntity(id, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                        UtilUUID.generarNuevoUUID(), null)));

        // Act
        var resultado = finder.obtener(observacion);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.estaOmitido()).isFalse();
    }

    @Test
    void debeDevolverVacio_cuandoLaObservacionNoExiste() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerPorObservacion(observacion)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(observacion);

        // Assert
        assertThat(resultado).isSameAs(ArtefactoDomain.VACIO);
    }
}
