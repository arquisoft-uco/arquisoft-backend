package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.exception.UltimoEntregableNoCerradoException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoUltimoEntregable;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UltimoEntregableCerradoRuleImplTest {

    private static final String TITULO_PROYECTO = "Plataforma de seguimiento de proyectos de grado";

    private final UltimoEntregableCerradoRuleImpl regla = new UltimoEntregableCerradoRuleImpl();

    @Test
    void debePasar_cuandoElUltimoEstaCerrado() {
        // Arrange
        var ultimo = new EstadoUltimoEntregable(
                UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, 2, EstadoEntregable.CERRADO);

        // Act & Assert
        assertThatCode(() -> regla.validar(ultimo)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"CANCELADO", "DISPONIBLE_EVALUACION"})
    void debeLanzar_cuandoElUltimoNoEstaCerrado(EstadoEntregable estado) {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var ultimo = new EstadoUltimoEntregable(proyectoGrado, TITULO_PROYECTO, 3, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(ultimo))
                .isInstanceOfSatisfying(UltimoEntregableNoCerradoException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(ProyectosCodes.ProyectoGrado.ULTIMO_ENTREGABLE_NO_CERRADO);
                    assertThat(exception.getError().getMessage())
                            .contains(TITULO_PROYECTO, "3", estado.getNombre())
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(exception.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
