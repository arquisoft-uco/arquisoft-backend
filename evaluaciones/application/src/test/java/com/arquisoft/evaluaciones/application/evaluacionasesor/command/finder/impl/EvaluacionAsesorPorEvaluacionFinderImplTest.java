package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;
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
class EvaluacionAsesorPorEvaluacionFinderImplTest {

    @Mock
    private EvaluacionAsesorOutputPort outputPort;

    @InjectMocks
    private EvaluacionAsesorPorEvaluacionFinderImpl finder;

    @Test
    void debeRetornarDominio_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorEvaluacion(evaluacion))
                .thenReturn(Optional.of(new EvaluacionAsesorEntity(id, evaluacion, asesor)));

        // Act
        var resultado = finder.obtener(evaluacion);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorEvaluacion(evaluacion)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(evaluacion);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
