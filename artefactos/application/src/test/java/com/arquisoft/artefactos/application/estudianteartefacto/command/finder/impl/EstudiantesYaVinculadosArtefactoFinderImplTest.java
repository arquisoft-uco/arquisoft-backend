package com.arquisoft.artefactos.application.estudianteartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesYaVinculadosArtefactoFinderImplTest {

    @Mock
    private EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;

    @InjectMocks
    private EstudiantesYaVinculadosArtefactoFinderImpl finder;

    @Test
    void debeDevolverLosEstudiantes_cuandoElPuertoDevuelveVinculos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var entrada = AgregacionEstudiantesArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), EstudianteArtefactoDomain.crear(artefacto, List.of(estudiante)));
        when(estudianteArtefactoOutputPort.obtenerVinculados(artefacto, List.of(estudiante)))
                .thenReturn(List.of(new EstudianteArtefactoEntity(UtilUUID.generarNuevoUUID(), artefacto, estudiante)));

        // Act
        var resultado = finder.obtener(entrada);

        // Assert
        assertThat(resultado).containsExactly(estudiante);
        verify(estudianteArtefactoOutputPort).obtenerVinculados(artefacto, List.of(estudiante));
    }
}
