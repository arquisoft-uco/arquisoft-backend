package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarEvaluacionFichaPerfilResponseMapperTest {

    @Test
    void debeMapearRegistradaYReactivadaAlMismoDto() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act
        var registrada = RegistrarEvaluacionFichaPerfilResponseMapper.toResponse(
                new InicioEvaluacionFichaPerfilResult.Registrada(evaluacion));
        var reactivada = RegistrarEvaluacionFichaPerfilResponseMapper.toResponse(
                new InicioEvaluacionFichaPerfilResult.Reactivada(evaluacion));

        // Assert
        assertThat(registrada.id()).isEqualTo(evaluacion);
        assertThat(reactivada).isEqualTo(registrada);
    }
}
