package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableVigenteYaExisteException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableVigente;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableVigenteUnicoRuleImplTest {

    private static final String NOMBRE_PROYECTO = "Plataforma de seguimiento de proyectos de grado";

    private final EntregableVigenteUnicoRuleImpl regla = new EntregableVigenteUnicoRuleImpl();

    @Test
    void debePasar_cuandoNoHayEntregableVigente() {
        // Arrange
        var existencia = new ExistenciaEntregableVigente(UtilUUID.generarNuevoUUID(), NOMBRE_PROYECTO, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoYaHayEntregableVigente() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaEntregableVigente(proyectoGrado, NOMBRE_PROYECTO, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(EntregableVigenteYaExisteException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EntregablesCodes.EntregableProyectoGrado.VIGENTE_YA_EXISTE);
                    assertThat(exception.getError().getMessage())
                            .contains(NOMBRE_PROYECTO)
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(exception.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
