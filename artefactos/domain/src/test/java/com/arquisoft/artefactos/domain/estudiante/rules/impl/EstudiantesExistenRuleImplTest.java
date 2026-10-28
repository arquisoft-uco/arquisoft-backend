package com.arquisoft.artefactos.domain.estudiante.rules.impl;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.artefactos.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesExistenRuleImplTest {

    private final EstudiantesExistenRuleImpl rule = new EstudiantesExistenRuleImpl();

    @Test
    void debePasar_cuandoTodosLosEstudiantesExisten() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = EstudianteDomain.reconstruir(id, "1", "Laura", "l@uco.edu.co", Instant.EPOCH, null);

        // Act & Assert
        assertThatCode(() -> rule.validar(new ExistenciaEstudiantes(List.of(id), List.of(encontrado))))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinUuidEnMensaje_cuandoFaltaUnEstudiante() {
        // Arrange
        var faltante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new ExistenciaEstudiantes(List.of(faltante), List.of())))
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .satisfies(ex -> {
                    var excepcion = (EstudianteNoEncontradoException) ex;
                    assertThat(excepcion.getCodigoError()).isEqualTo(ArtefactosCodes.Estudiante.NO_ENCONTRADO);
                    assertThat(excepcion.getError().getMessage()).doesNotContain(faltante.toString());
                });
    }
}
