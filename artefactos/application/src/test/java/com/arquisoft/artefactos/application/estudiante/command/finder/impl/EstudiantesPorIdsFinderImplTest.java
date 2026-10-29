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
class EstudiantesPorIdsFinderImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudiantesPorIdsFinderImpl finder;

    @Test
    void debeMapearADomainsIncluyendoLosDadosDeBaja_cuandoElPuertoDevuelveEstudiantes() {
        // Arrange
        var vigente = UtilUUID.generarNuevoUUID();
        var eliminado = UtilUUID.generarNuevoUUID();
        when(estudianteOutputPort.obtenerPorIds(List.of(vigente, eliminado))).thenReturn(List.of(
                new EstudianteEntity(vigente, "1", "Laura", "l@uco.edu.co", Instant.EPOCH, null),
                new EstudianteEntity(eliminado, "2", "Pedro", "p@uco.edu.co", Instant.EPOCH, Instant.now())));

        // Act
        var resultado = finder.obtener(List.of(vigente, eliminado));

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Laura");
        assertThat(resultado.get(0).estaEliminado()).isFalse();
        assertThat(resultado.get(1).getEmail()).isEqualTo("p@uco.edu.co");
        assertThat(resultado.get(1).estaEliminado()).isTrue();
    }
}
