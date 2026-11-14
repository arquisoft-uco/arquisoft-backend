package com.arquisoft.entregables.application.proyectogrado.command.result.mapper;

import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.FinalizacionProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FinalizacionProyectoGradoResultMapperTest {

    private final FinalizacionProyectoGradoDomain finalizacion = FinalizacionProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), PurgaArtefactosProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID()));

    private final PurgaArtefactosProyectoGradoResult purga =
            new PurgaArtefactosProyectoGradoResult(finalizacion.getProyectoGrado(), 4);

    @Test
    void debeMapearFinalizado_conElProyectoGradoYLosArtefactosEliminados() {
        // Act
        var resultado = FinalizacionProyectoGradoResultMapper.toResultFinalizado(finalizacion, purga);

        // Assert
        assertThat(resultado.proyectoGrado()).isEqualTo(finalizacion.getProyectoGrado());
        assertThat(resultado.artefactosEliminados()).isEqualTo(4);
    }

    @Test
    void debeMapearDuplicado_conElProyectoGradoYLosArtefactosEliminados() {
        // Act
        var resultado = FinalizacionProyectoGradoResultMapper.toResultDuplicado(finalizacion, purga);

        // Assert
        assertThat(resultado.proyectoGrado()).isEqualTo(finalizacion.getProyectoGrado());
        assertThat(resultado.artefactosEliminados()).isEqualTo(4);
    }
}
