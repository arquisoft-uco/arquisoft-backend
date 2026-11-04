package com.arquisoft.artefactos.application.estudianteartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesVigentesVinculadosArtefactoFinderImplTest {

    @Mock
    private EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;

    @InjectMocks
    private EstudiantesVigentesVinculadosArtefactoFinderImpl finder;

    @Test
    void debeConsultarPorElIdDelArtefacto_yDevolverLosVigentes() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        when(estudianteArtefactoOutputPort.obtenerVigentesVinculados(artefacto)).thenReturn(List.of(vigente));

        // Act
        var resultado = finder.obtener(artefacto);

        // Assert
        assertThat(resultado).containsExactly(vigente);
    }
}
