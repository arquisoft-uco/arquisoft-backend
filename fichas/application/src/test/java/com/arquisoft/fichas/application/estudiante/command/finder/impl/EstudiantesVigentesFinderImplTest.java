package com.arquisoft.fichas.application.estudiante.command.finder.impl;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesVigentesFinderImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudiantesVigentesFinderImpl finder;

    @Test
    void debeConsultarEnUnSoloViaje_cuandoHayVariosEstudiantes() {
        // Arrange
        var ids = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var vigentes = List.of(ids.get(0), ids.get(2));
        when(estudianteOutputPort.buscarIdsVigentes(ids)).thenReturn(vigentes);

        // Act
        var resultado = finder.obtener(ids);

        // Assert
        assertThat(resultado).isEqualTo(vigentes);
        verify(estudianteOutputPort, times(1)).buscarIdsVigentes(any());
    }

    @Test
    void debeDevolverListaVaciaSinConsultar_cuandoNoHayEstudiantes() {
        // Act
        var resultado = finder.obtener(List.of());

        // Assert
        assertThat(resultado).isEmpty();
        verify(estudianteOutputPort, never()).buscarIdsVigentes(any());
    }
}
