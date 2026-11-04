package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteYaVinculadoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.VinculosEstudiantesProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesNoVinculadosRuleImplTest {

    private final EstudiantesNoVinculadosRuleImpl regla = new EstudiantesNoVinculadosRuleImpl();

    @Test
    void debePasar_cuandoNoHayVinculados() {
        // Arrange
        var vinculos = new VinculosEstudiantesProyectoGrado(List.of(), List.of());

        // Act & Assert
        assertThatCode(() -> regla.validar(vinculos)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarYaVinculadoNombrandoALosEstudiantes_cuandoHayVinculados() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var vinculos = new VinculosEstudiantesProyectoGrado(
                List.of(primero, segundo), List.of("Ana Pérez", "Luis Gómez"));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vinculos))
                .isInstanceOfSatisfying(EstudianteYaVinculadoException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_YA_VINCULADO);
                    assertThat(ex.getError().getMessage())
                            .contains("Ana Pérez")
                            .contains("Luis Gómez")
                            .doesNotContain(primero.toString())
                            .doesNotContain(segundo.toString());
                });
    }
}
