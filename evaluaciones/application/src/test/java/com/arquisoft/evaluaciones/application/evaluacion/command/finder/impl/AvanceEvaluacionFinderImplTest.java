package com.arquisoft.evaluaciones.application.evaluacion.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.AvanceEvaluacionEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain;
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
class AvanceEvaluacionFinderImplTest {

    @Mock
    private EvaluacionOutputPort outputPort;

    @InjectMocks
    private AvanceEvaluacionFinderImpl finder;

    @Test
    void debeReconstruirElAvance_cuandoLaEvaluacionExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAvance(evaluacion)).thenReturn(Optional.of(
                new AvanceEvaluacionEntity(evaluacion, "EN_PROGRESO", entregable, "Proyecto", 3, 2, 1, true)));

        // Act
        var avance = finder.obtener(evaluacion);

        // Assert
        assertThat(avance.getEstado()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
        assertThat(avance.getEntregable().id()).isEqualTo(entregable);
        assertThat(avance.getEntregable().proyecto()).isEqualTo("Proyecto");
        assertThat(avance.getEntregable().version()).isEqualTo(3);
        assertThat(avance.getProgreso().juradosVigentes()).isEqualTo(2);
        assertThat(avance.getProgreso().juradosEvaluados()).isEqualTo(1);
        assertThat(avance.getProgreso().asesorCompleto()).isTrue();
    }

    @Test
    void debeDevolverVacio_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAvance(evaluacion)).thenReturn(Optional.empty());

        // Act
        var avance = finder.obtener(evaluacion);

        // Assert
        assertThat(avance).isSameAs(AvanceEvaluacionDomain.VACIO);
    }
}
