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
    void debePasar_cuandoLosEnviadosIgualanALosEstudiantesDelProyecto() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var cupo = new CupoEstudiantesArtefacto(List.of(primero, segundo), List.of(), List.of(primero, segundo));

        // Act & Assert
        assertThatCode(() -> rule.validar(cupo)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoLosPreviosNoEnviadosSuperanElTope() {
        // Arrange
        var enviado = UtilUUID.generarNuevoUUID();
        var previoUno = UtilUUID.generarNuevoUUID();
        var previoDos = UtilUUID.generarNuevoUUID();
        var cupo = new CupoEstudiantesArtefacto(
                List.of(enviado), List.of(enviado, previoUno, previoDos), List.of(enviado));

        // Act & Assert
        assertThatCode(() -> rule.validar(cupo)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEstudiantesSuperanProyecto_cuandoLosEnviadosSuperanAlProyecto() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var cupo = new CupoEstudiantesArtefacto(List.of(primero, segundo), List.of(primero), List.of(primero));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(cupo))
                .isInstanceOf(EstudiantesSuperanProyectoException.class)
                .satisfies(ex -> assertThat(((EstudiantesSuperanProyectoException) ex).getCodigoError())
                        .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_SUPERAN_PROYECTO));
    }
}
