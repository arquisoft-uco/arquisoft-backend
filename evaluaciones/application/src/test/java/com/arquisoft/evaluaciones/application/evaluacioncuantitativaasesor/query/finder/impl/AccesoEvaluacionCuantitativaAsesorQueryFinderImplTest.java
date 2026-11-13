package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.EvaluacionCuantitativaAsesorAccesoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.entity.EvaluacionCuantitativaAsesorAccesoEntity;
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
class AccesoEvaluacionCuantitativaAsesorQueryFinderImplTest {

    @Mock
    private EvaluacionCuantitativaAsesorAccesoQueryOutputPort outputPort;

    @InjectMocks
    private AccesoEvaluacionCuantitativaAsesorQueryFinderImpl finder;

    @Test
    void debeDevolverElDomain_cuandoLaEvaluacionExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAccesoPorId(id)).thenReturn(Optional.of(
                new EvaluacionCuantitativaAsesorAccesoEntity(id, evaluacionAsesor, asesor)));

        // Act
        var acceso = finder.obtener(id);

        // Assert
        assertThat(acceso.esVacio()).isFalse();
        assertThat(acceso.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(acceso.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeDevolverVacio_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAccesoPorId(id)).thenReturn(Optional.empty());

        // Act
        var acceso = finder.obtener(id);

        // Assert
        assertThat(acceso.esVacio()).isTrue();
    }
}
