package com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InicioEvaluacionFichaPerfilResultMapperTest {

    @Test
    void debeConstruirRegistradaYReactivadaConLaEvaluacionRecibida() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act
        var registrada = InicioEvaluacionFichaPerfilResultMapper.toResultRegistrada(evaluacion);
        var reactivada = InicioEvaluacionFichaPerfilResultMapper.toResultReactivada(evaluacion);

        // Assert
        assertThat(registrada).isEqualTo(new InicioEvaluacionFichaPerfilResult.Registrada(evaluacion));
        assertThat(reactivada).isEqualTo(new InicioEvaluacionFichaPerfilResult.Reactivada(evaluacion));
    }
}
