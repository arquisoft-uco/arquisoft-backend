package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.ObservacionEvaluacionDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepresentantePropietarioObservacionEvaluacionFinderImplTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @InjectMocks
    private RepresentantePropietarioObservacionEvaluacionFinderImpl finder;

    @Test
    void debeRetornarPropiedadDelPuerto_conEvaluacionYRepresentanteDeLaAgregacion() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var agregacion = AgregacionObservacionEvaluacionDomain.crear(
                ObservacionEvaluacionDomain.crear(evaluacionFichaPerfil, "Observación válida"), representanteComite);
        when(evaluacionFichaPerfilOutputPort.esRepresentantePropietario(evaluacionFichaPerfil, representanteComite))
                .thenReturn(true);

        // Act
        var esPropietario = finder.obtener(agregacion);

        // Assert
        assertThat(esPropietario).isTrue();
    }
}
