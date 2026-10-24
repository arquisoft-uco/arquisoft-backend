package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.none;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SinDecisionAccesoOutputAdapterTest {

    @Test
    void debeRetornarPermitida_cuandoDecide() {
        // Act
        var decision = new SinDecisionAccesoOutputAdapter().decidir(new SolicitudAcceso("GET", "/x", "t", "e", "c"));

        // Assert
        assertThat(decision).isEqualTo(new DecisionAcceso.Permitida(""));
    }
}
