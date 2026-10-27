package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
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
class ArtefactoBloqueadoPorIdFinderImplTest {

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @InjectMocks
    private ArtefactoBloqueadoPorIdFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerBloqueadoPorId(id)).thenReturn(Optional.of(
                new ArtefactoEntity(id, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesor)));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerBloqueadoPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
