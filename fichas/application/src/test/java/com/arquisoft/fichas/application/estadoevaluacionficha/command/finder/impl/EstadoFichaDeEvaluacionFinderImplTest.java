package com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EstadoFichaDeEvaluacionEntity;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
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
class EstadoFichaDeEvaluacionFinderImplTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @InjectMocks
    private EstadoFichaDeEvaluacionFinderImpl finder;

    @Test
    void debeRetornarEstadoActualFicha_cuandoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        when(evaluacionFichaPerfilOutputPort.obtenerEstadoFichaActual(evaluacion))
                .thenReturn(Optional.of(new EstadoFichaDeEvaluacionEntity(
                        ficha, EstadoFicha.DISPONIBLE_PARA_EVALUACION.getId())));

        // Act
        var resultado = finder.obtener(evaluacion);

        // Assert
        assertThat(resultado)
                .isEqualTo(new EstadoActualFicha(ficha, EstadoFicha.DISPONIBLE_PARA_EVALUACION));
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(evaluacionFichaPerfilOutputPort.obtenerEstadoFichaActual(evaluacion)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(evaluacion);

        // Assert
        assertThat(resultado).isSameAs(EstadoActualFicha.VACIO);
    }
}
