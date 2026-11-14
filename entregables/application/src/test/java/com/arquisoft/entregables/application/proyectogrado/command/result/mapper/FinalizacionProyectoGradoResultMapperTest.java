package com.arquisoft.entregables.application.proyectogrado.command.result.mapper;

import com.arquisoft.entregables.domain.proyectogrado.FinalizacionProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FinalizacionProyectoGradoResultMapperTest {

    private final FinalizacionProyectoGradoDomain finalizacion = FinalizacionProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID());

    @Test
    void debeMapearFinalizado_cuandoSeFinalizaElProyectoGrado() {
        // Act
        var resultado = FinalizacionProyectoGradoResultMapper.toResultFinalizado(finalizacion);

        // Assert
        assertThat(resultado.proyectoGrado()).isEqualTo(finalizacion.getProyectoGrado());
    }

    @Test
    void debeMapearDuplicado_cuandoElProyectoGradoYaEstabaFinalizado() {
        // Act
        var resultado = FinalizacionProyectoGradoResultMapper.toResultDuplicado(finalizacion);

        // Assert
        assertThat(resultado.proyectoGrado()).isEqualTo(finalizacion.getProyectoGrado());
    }
}
