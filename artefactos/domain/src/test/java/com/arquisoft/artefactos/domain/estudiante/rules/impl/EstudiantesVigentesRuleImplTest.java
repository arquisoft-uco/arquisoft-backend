package com.arquisoft.artefactos.domain.estudiante.rules.impl;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoVigenteException;
import com.arquisoft.artefactos.domain.estudiante.model.VigenciaEstudiantes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesVigentesRuleImplTest {

    private static final String NOMBRE = "Laura Gomez";

    private final EstudiantesVigentesRuleImpl rule = new EstudiantesVigentesRuleImpl();

    @Test
    void debePasar_cuandoNingunEstudianteEstaDadoDeBaja() {
        // Arrange
        var vigente = EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "1", NOMBRE, "l@uco.edu.co", Instant.EPOCH, null);

        // Act & Assert
        assertThatCode(() -> rule.validar(new VigenciaEstudiantes(List.of(vigente)))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConNombreSinUuid_cuandoUnEstudianteEstaDadoDeBaja() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminado = EstudianteDomain.reconstruir(id, "1", NOMBRE, "l@uco.edu.co", Instant.EPOCH, Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new VigenciaEstudiantes(List.of(eliminado))))
                .isInstanceOf(EstudianteNoVigenteException.class)
                .satisfies(ex -> {
                    var excepcion = (EstudianteNoVigenteException) ex;
                    assertThat(excepcion.getCodigoError()).isEqualTo(ArtefactosCodes.Estudiante.NO_VIGENTE);
                    assertThat(excepcion.getError().getMessage()).contains(NOMBRE).doesNotContain(id.toString());
                });
    }
}
