package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteDuplicadoException;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesSinDuplicadosRuleImplTest {

    private final EstudiantesSinDuplicadosRuleImpl regla = new EstudiantesSinDuplicadosRuleImpl();

    @Test
    void debeLanzarExcepcionNombrandoElDuplicado_cuandoUnEstudianteSeRepite() {
        // Arrange
        var repetido = UUID.randomUUID();
        var estudiantes = List.of(repetido, UUID.randomUUID(), repetido);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estudiantes))
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(repetido.toString())
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_DUPLICADO);
    }

    @Test
    void debePasar_cuandoTodosLosEstudiantesSonDistintos() {
        // Arrange
        var estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        // Act & Assert
        assertThatCode(() -> regla.validar(estudiantes)).doesNotThrowAnyException();
    }
}
