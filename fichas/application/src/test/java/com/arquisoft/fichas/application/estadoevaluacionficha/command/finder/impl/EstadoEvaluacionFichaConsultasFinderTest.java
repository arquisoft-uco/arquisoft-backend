package com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoEvaluacionFichaConsultasFinderTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @InjectMocks
    private EvaluacionFichaExisteFinderImpl evaluacionExisteFinder;

    @InjectMocks
    private RepresentantePropietarioEvaluacionFinderImpl propietarioFinder;

    @InjectMocks
    private EstadoEnEvaluacionExisteFinderImpl estadoEnEvaluacionFinder;

    private final UUID evaluacion = UUID.randomUUID();
    private final UUID representante = UUID.randomUUID();

    @Test
    void debeTrasladarLaExistenciaDeLaEvaluacion_cuandoExiste() {
        // Arrange
        when(evaluacionFichaPerfilOutputPort.existePorId(evaluacion)).thenReturn(true);

        // Act & Assert
        assertThat(evaluacionExisteFinder.obtener(evaluacion)).isTrue();
    }

    @Test
    void debeTrasladarLaAusenciaDeLaEvaluacion_cuandoNoExiste() {
        // Arrange
        when(evaluacionFichaPerfilOutputPort.existePorId(evaluacion)).thenReturn(false);

        // Act & Assert
        assertThat(evaluacionExisteFinder.obtener(evaluacion)).isFalse();
    }

    @Test
    void debeTrasladarLaPropiedadDeLaEvaluacion_cuandoElRepresentanteEsDueno() {
        // Arrange
        var entrada = entrada();
        when(evaluacionFichaPerfilOutputPort.esRepresentantePropietario(evaluacion, representante))
                .thenReturn(true);

        // Act & Assert
        assertThat(propietarioFinder.obtener(entrada)).isTrue();
    }

    @Test
    void debeTrasladarQueElEstadoYaEstaRegistrado_cuandoElPuertoResponde() {
        // Arrange
        var entrada = entrada();
        when(estadoEvaluacionFichaOutputPort.existePorEvaluacionYEstado(
                evaluacion, EstadoEvaluacion.APROBADA.getId())).thenReturn(true);

        // Act & Assert
        assertThat(estadoEnEvaluacionFinder.obtener(entrada)).isTrue();
    }

    @Test
    void debeTrasladarQueElEstadoNoEstaRegistrado_cuandoElPuertoResponde() {
        // Arrange
        var entrada = entrada();
        when(estadoEvaluacionFichaOutputPort.existePorEvaluacionYEstado(
                evaluacion, EstadoEvaluacion.APROBADA.getId())).thenReturn(false);

        // Act & Assert
        assertThat(estadoEnEvaluacionFinder.obtener(entrada)).isFalse();
    }

    private AgregacionEstadoEvaluacionFichaDomain entrada() {
        return AgregacionEstadoEvaluacionFichaDomain.crear(
                evaluacion, EstadoEvaluacion.APROBADA.getId(), representante);
    }
}
