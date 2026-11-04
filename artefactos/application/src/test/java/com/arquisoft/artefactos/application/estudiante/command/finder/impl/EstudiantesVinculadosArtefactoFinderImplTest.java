package com.arquisoft.artefactos.application.estudiante.command.finder.impl;

import com.arquisoft.artefactos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesVinculadosArtefactoFinderImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudiantesVinculadosArtefactoFinderImpl finder;

    @Test
    void debeMapearLosVinculadosADomain_cuandoElArtefactoTieneEstudiantes() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        when(estudianteOutputPort.obtenerVigentesVinculadosAlArtefacto(artefacto)).thenReturn(List.of(
                new EstudianteEntity(primero, "1", "Laura Gomez", "laura@uco.edu.co", Instant.EPOCH, null),
                new EstudianteEntity(segundo, "2", "Pedro Diaz", "pedro@uco.edu.co", Instant.EPOCH, null)));

        // Act
        var resultado = finder.obtener(artefacto);

        // Assert
        assertThat(resultado).extracting(estudiante -> estudiante.getId()).containsExactly(primero, segundo);
        assertThat(resultado).extracting(estudiante -> estudiante.getNombre())
                .containsExactly("Laura Gomez", "Pedro Diaz");
    }

    @Test
    void debeDevolverListaVacia_cuandoElArtefactoNoTieneEstudiantes() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        when(estudianteOutputPort.obtenerVigentesVinculadosAlArtefacto(artefacto)).thenReturn(List.of());

        // Act
        var resultado = finder.obtener(artefacto);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
