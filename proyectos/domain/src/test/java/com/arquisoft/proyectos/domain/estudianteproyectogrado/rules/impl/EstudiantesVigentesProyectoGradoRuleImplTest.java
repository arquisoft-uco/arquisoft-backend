package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudiantesNoVigentesException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.VigenciaEstudiantesProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesVigentesProyectoGradoRuleImplTest {

    private final EstudiantesVigentesProyectoGradoRuleImpl regla = new EstudiantesVigentesProyectoGradoRuleImpl();

    @Test
    void debeLanzarExcepcionNombrandoLosFaltantes_cuandoAlgunEstudianteNoEstaVigente() {
        // Arrange
        var vigente = UUID.randomUUID();
        var faltante = UUID.randomUUID();
        var vigencia = new VigenciaEstudiantesProyectoGrado(Set.of(vigente, faltante), Set.of(vigente));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(EstudiantesNoVigentesException.class)
                .hasMessageContaining(faltante.toString())
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_NO_VIGENTES);
    }

    @Test
    void debePasar_cuandoTodosLosEstudiantesSolicitadosEstanVigentes() {
        // Arrange
        var a = UUID.randomUUID();
        var b = UUID.randomUUID();
        var vigencia = new VigenciaEstudiantesProyectoGrado(Set.of(a, b), Set.of(a, b));

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }
}
