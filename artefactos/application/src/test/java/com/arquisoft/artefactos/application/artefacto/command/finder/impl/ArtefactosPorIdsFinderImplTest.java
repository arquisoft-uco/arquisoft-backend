package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
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
class ArtefactosPorIdsFinderImplTest {

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @InjectMocks
    private ArtefactosPorIdsFinderImpl finder;

    @Test
    void debeMapearADomains_cuandoElPuertoDevuelveArtefactos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerPorIds(List.of(id)))
                .thenReturn(List.of(new ArtefactoEntity(id, proyecto, version, asesor)));

        // Act
        var resultado = finder.obtener(List.of(id));

        // Assert
        assertThat(resultado).singleElement().satisfies(artefacto -> {
            assertThat(artefacto.getId()).isEqualTo(id);
            assertThat(artefacto.getProyectoGrado()).isEqualTo(proyecto);
            assertThat(artefacto.getVersionRepositorioArtefacto()).isEqualTo(version);
            assertThat(artefacto.getAsesor()).isEqualTo(asesor);
        });
    }
}
