package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteYaVinculadoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.VinculosEstudiantesProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesNoVinculadosRuleImplTest {

    private final EstudiantesNoVinculadosRuleImpl regla = new EstudiantesNoVinculadosRuleImpl();

    @Test
    void debePasar_cuandoNoHayVinculados() {
        // Arrange
        var vinculos = new VinculosEstudiantesProyectoGrado(List.of());

        // Act & Assert
        assertThatCode(() -> regla.validar(vinculos)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarYaVinculado_cuandoHayVinculados() {
        // Arrange
        var vinculos = new VinculosEstudiantesProyectoGrado(
                List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vinculos))
                .isInstanceOf(EstudianteYaVinculadoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_YA_VINCULADO);
    }
}
