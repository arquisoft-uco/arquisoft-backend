package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoConEntregableVigenteException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoUltimoEntregable;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoSinEntregableVigenteRuleImplTest {

    private static final String TITULO_PROYECTO = "Plataforma de seguimiento de proyectos de grado";

    private final ProyectoGradoSinEntregableVigenteRuleImpl regla = new ProyectoGradoSinEntregableVigenteRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"NUEVO", "RECHAZADO", "CANCELADO", "VACIO"})
    void debePasar_cuandoElEstadoNoBloquea(EstadoEntregable estado) {
        // Arrange
        var ultimo = new EstadoUltimoEntregable(UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, 2, estado);

        // Act & Assert
        assertThatCode(() -> regla.validar(ultimo)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class,
            names = {"DISPONIBLE_EVALUACION", "APROBADO", "APROBADO_CON_OBSERVACIONES", "CERRADO"})
    void debeLanzar_cuandoElEstadoBloquea(EstadoEntregable estado) {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var ultimo = new EstadoUltimoEntregable(proyectoGrado, TITULO_PROYECTO, 3, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(ultimo))
                .isInstanceOfSatisfying(ProyectoGradoConEntregableVigenteException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(ProyectosCodes.ProyectoGrado.CON_ENTREGABLE_VIGENTE);
                    assertThat(exception.getError().getMessage())
                            .contains(TITULO_PROYECTO)
                            .contains("3")
                            .contains(estado.getNombre().toLowerCase())
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(exception.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
