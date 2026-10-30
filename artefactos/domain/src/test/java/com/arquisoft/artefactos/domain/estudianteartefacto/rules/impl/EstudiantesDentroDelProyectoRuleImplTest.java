package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudiantesSuperanProyectoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.CupoEstudiantesArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesDentroDelProyectoRuleImplTest {

    private final EstudiantesDentroDelProyectoRuleImpl rule = new EstudiantesDentroDelProyectoRuleImpl();

    @Test
    void debePasar_cuandoLaUnionIgualaALosEstudiantesDelProyecto() {
        // Arrange
        var conservado = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var cupo = new CupoEstudiantesArtefacto(List.of(nuevo), List.of(conservado), List.of(conservado, nuevo));

        // Act & Assert
        assertThatCode(() -> rule.validar(cupo)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoUnSolicitadoYaEstaVinculado() {
        // Arrange
        var repetido = UtilUUID.generarNuevoUUID();
        var cupo = new CupoEstudiantesArtefacto(List.of(repetido), List.of(repetido), List.of(repetido));

        // Act & Assert
        assertThatCode(() -> rule.validar(cupo)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEstudiantesSuperanProyecto_cuandoLaUnionSuperaAlProyecto() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var cupo = new CupoEstudiantesArtefacto(List.of(segundo), List.of(primero), List.of(primero));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(cupo))
                .isInstanceOf(EstudiantesSuperanProyectoException.class)
                .satisfies(ex -> assertThat(((EstudiantesSuperanProyectoException) ex).getCodigoError())
                        .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_SUPERAN_PROYECTO));
    }
}
