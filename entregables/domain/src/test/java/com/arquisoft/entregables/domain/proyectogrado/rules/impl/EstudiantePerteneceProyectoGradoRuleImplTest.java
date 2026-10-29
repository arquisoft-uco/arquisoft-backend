package com.arquisoft.entregables.domain.proyectogrado.rules.impl;

import com.arquisoft.entregables.domain.proyectogrado.exception.EstudianteNoPerteneceProyectoGradoException;
import com.arquisoft.entregables.domain.proyectogrado.model.PertenenciaProyectoGrado;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantePerteneceProyectoGradoRuleImplTest {

    private final EstudiantePerteneceProyectoGradoRuleImpl regla = new EstudiantePerteneceProyectoGradoRuleImpl();

    @Test
    void debeLanzar_cuandoElEstudianteNoPertenece() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var pertenencia = new PertenenciaProyectoGrado(proyecto, estudiante, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(pertenencia))
                .isInstanceOf(EstudianteNoPerteneceProyectoGradoException.class)
                .extracting("codigoError")
                .isEqualTo(EntregablesCodes.ProyectoGrado.ESTUDIANTE_NO_PERTENECE);
        assertThatThrownBy(() -> regla.validar(pertenencia))
                .isInstanceOfSatisfying(DomainException.class, ex -> {
                    assertThat(ex.getError().getMessage()).doesNotContain(proyecto.toString(), estudiante.toString());
                    assertThat(ex.getError().getDetalle()).contains(proyecto.toString(), estudiante.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoElEstudiantePertenece() {
        // Arrange
        var pertenencia = new PertenenciaProyectoGrado(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(pertenencia)).doesNotThrowAnyException();
    }
}
