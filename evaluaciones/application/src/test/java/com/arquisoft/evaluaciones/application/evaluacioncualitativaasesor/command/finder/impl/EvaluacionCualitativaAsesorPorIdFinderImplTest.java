package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;
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
class EvaluacionCualitativaAsesorPorIdFinderImplTest {

    @Mock
    private EvaluacionCualitativaAsesorOutputPort outputPort;

    @InjectMocks
    private EvaluacionCualitativaAsesorPorIdFinderImpl finder;

    @Test
    void debeDevolverDomain_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.of(
                new EvaluacionCualitativaAsesorEntity(id, evaluacionAsesor, item, criterio)));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(resultado.getItem()).isEqualTo(item);
        assertThat(resultado.getCriterio()).isEqualTo(criterio);
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
