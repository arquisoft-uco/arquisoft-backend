package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionCualitativaJuradoPorIdFinderImplTest {

    @Mock
    private EvaluacionCualitativaJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionCualitativaJuradoPorIdFinderImpl finder;

    @Test
    void debeDevolverDomain_cuandoLaEvaluacionExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorId(id))
                .thenReturn(Optional.of(new EvaluacionCualitativaJuradoEntity(id, evaluacionJurado, item, criterio)));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(resultado.getItem()).isEqualTo(item);
        assertThat(resultado.getCriterio()).isEqualTo(criterio);
        verify(outputPort).obtenerPorId(id);
    }

    @Test
    void debeDevolverVacio_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(EvaluacionCualitativaJuradoDomain.VACIO);
    }
}
