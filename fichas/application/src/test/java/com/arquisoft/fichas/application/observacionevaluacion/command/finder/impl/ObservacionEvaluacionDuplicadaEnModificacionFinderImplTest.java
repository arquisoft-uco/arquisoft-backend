package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionEvaluacionDuplicadaEnModificacionFinderImplTest {

    @Mock
    private ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @InjectMocks
    private ObservacionEvaluacionDuplicadaEnModificacionFinderImpl finder;

    @Test
    void debeRetornarDuplicidadDelPuerto_conIdYTextoDeLaModificacion() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionObservacionEvaluacionDomain.crear(
                observacionEvaluacion, "  Nuevo texto de la observación  ", UtilUUID.generarNuevoUUID());
        when(observacionEvaluacionOutputPort.existeOtraConMismoTexto(
                observacionEvaluacion, "Nuevo texto de la observación")).thenReturn(true);

        // Act
        var duplicada = finder.obtener(modificacion);

        // Assert
        assertThat(duplicada).isTrue();
    }
}
