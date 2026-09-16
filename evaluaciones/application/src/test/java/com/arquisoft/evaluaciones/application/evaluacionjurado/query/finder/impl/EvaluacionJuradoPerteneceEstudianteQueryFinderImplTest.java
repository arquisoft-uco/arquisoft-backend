package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.estudiantesproyectogrado.query.secondaryport.EstudiantesProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionJuradoPerteneceEstudianteQueryFinderImplTest {

    @Mock
    private EvaluacionJuradoAccesoQueryOutputPort evaluacionJuradoAccesoQueryOutputPort;

    @Mock
    private EstudiantesProyectoGradoOutputPort estudiantesProyectoGradoOutputPort;

    @InjectMocks
    private EvaluacionJuradoPerteneceEstudianteQueryFinderImpl finder;

    @Test
    void debeRetornarTrue_cuandoElEstudianteEstaEnLaListaDelProyecto() {
        // Arrange
        var criteria = new EvaluacionCualitativaJuradoCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(evaluacionJuradoAccesoQueryOutputPort.obtenerProyecto(criteria.evaluacionJuradoId()))
                .thenReturn(Optional.of("Proyecto"));
        when(estudiantesProyectoGradoOutputPort.obtenerEstudiantes("Proyecto"))
                .thenReturn(List.of(criteria.estudianteId()));

        // Act
        Boolean resultado = finder.obtener(criteria);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElEstudianteNoEstaEnLaListaDelProyecto() {
        // Arrange
        var criteria = new EvaluacionCualitativaJuradoCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(evaluacionJuradoAccesoQueryOutputPort.obtenerProyecto(criteria.evaluacionJuradoId()))
                .thenReturn(Optional.of("Proyecto"));
        when(estudiantesProyectoGradoOutputPort.obtenerEstudiantes("Proyecto"))
                .thenReturn(List.of(UUID.randomUUID()));

        // Act
        Boolean resultado = finder.obtener(criteria);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    void debeRetornarFalse_yNoConsultarEstudiantes_cuandoLaEvaluacionJuradoNoTieneProyecto() {
        // Arrange
        var criteria = new EvaluacionCualitativaJuradoCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(evaluacionJuradoAccesoQueryOutputPort.obtenerProyecto(criteria.evaluacionJuradoId()))
                .thenReturn(Optional.empty());

        // Act
        Boolean resultado = finder.obtener(criteria);

        // Assert
        assertThat(resultado).isFalse();
        verify(estudiantesProyectoGradoOutputPort, never()).obtenerEstudiantes(any());
    }
}
