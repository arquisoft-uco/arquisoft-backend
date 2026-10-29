package com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarEstudiantesProyectoGradoMapperTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Test
    void debeCrearUnVinculoPorEstudianteConElMismoOcurridoEn_cuandoMapeaElCommand() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var command = new AgregarEstudiantesProyectoGradoCommand(proyectoGrado, List.of(primero, segundo), OCURRIDO_EN);

        // Act
        var agregacion = AgregarEstudiantesProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(agregacion.getVinculos()).hasSize(2)
                .extracting(EstudianteProyectoGradoDomain::getEstudiante).containsExactly(primero, segundo);
        assertThat(agregacion.getVinculos())
                .allSatisfy(vinculo -> {
                    assertThat(vinculo.getProyectoGrado()).isEqualTo(proyectoGrado);
                    assertThat(vinculo.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
                    assertThat(vinculo.estaEliminado()).isFalse();
                });
    }
}
