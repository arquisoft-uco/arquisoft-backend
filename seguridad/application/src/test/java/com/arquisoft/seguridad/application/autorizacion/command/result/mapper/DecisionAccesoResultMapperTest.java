package com.arquisoft.seguridad.application.autorizacion.command.result.mapper;

import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionAccesoResultMapperTest {

    @Test
    void debeMapearCadaVariante_cuandoLaDecisionLlegaDelPuerto() {
        // Act / Assert
        assertThat(DecisionAccesoResultMapper.toResult(new DecisionAcceso.Permitida("d-1")))
                .isEqualTo(new DecisionAccesoResult.Permitida("d-1"));
        assertThat(DecisionAccesoResultMapper.toResult(new DecisionAcceso.NoAutenticada()))
                .isInstanceOf(DecisionAccesoResult.NoAutenticada.class);
        assertThat(DecisionAccesoResultMapper.toResult(new DecisionAcceso.Denegada()))
                .isInstanceOf(DecisionAccesoResult.Denegada.class);
    }
}
