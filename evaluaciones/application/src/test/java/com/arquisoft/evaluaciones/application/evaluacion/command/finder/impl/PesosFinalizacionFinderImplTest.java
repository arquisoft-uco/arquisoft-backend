package com.arquisoft.evaluaciones.application.evaluacion.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.ConfiguracionFinalizacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.PesosFinalizacionEntity;
import com.arquisoft.evaluaciones.domain.evaluacion.model.PesosFinalizacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PesosFinalizacionFinderImplTest {

    @Mock
    private ConfiguracionFinalizacionOutputPort outputPort;

    @InjectMocks
    private PesosFinalizacionFinderImpl finder;

    @Test
    void debeMapearLosPesosConfigurados_cuandoSeConsulta() {
        // Arrange
        when(outputPort.obtenerPesos())
                .thenReturn(new PesosFinalizacionEntity(new BigDecimal("2"), new BigDecimal("1")));

        // Act
        var pesos = finder.obtener();

        // Assert
        assertThat(pesos).isEqualTo(new PesosFinalizacion(new BigDecimal("2"), new BigDecimal("1")));
    }
}
