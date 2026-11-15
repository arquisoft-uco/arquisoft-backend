package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
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
class ItemDeEvaluacionCuantitativaAsesorFinderImplTest {

    @Mock
    private EvaluacionCuantitativaAsesorOutputPort outputPort;

    @InjectMocks
    private ItemDeEvaluacionCuantitativaAsesorFinderImpl finder;

    @Test
    void debeRetornarElItem_cuandoExiste() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerItem(evaluacionCuantitativaAsesor)).thenReturn(Optional.of(item));

        // Act
        var resultado = finder.obtener(evaluacionCuantitativaAsesor);

        // Assert
        assertThat(resultado).isEqualTo(item);
    }

    @Test
    void debeRetornarUuidPorDefecto_cuandoNoExiste() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerItem(evaluacionCuantitativaAsesor)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(evaluacionCuantitativaAsesor);

        // Assert
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }
}
