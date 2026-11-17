package com.arquisoft.biblioteca.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.biblioteca.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableProyectoGradoExisteRuleImplTest {

    private final EntregableProyectoGradoExisteRuleImpl rule = new EntregableProyectoGradoExisteRuleImpl();

    @Test
    void debeLanzarNoEncontrado_cuandoElEntregableNoExiste() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> rule.validar(new ExistenciaEntregableProyectoGrado(entregable, true)))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rule.validar(new ExistenciaEntregableProyectoGrado(entregable, false)))
                .isInstanceOfSatisfying(EntregableProyectoGradoNoEncontradoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(BibliotecaCodes.EntregableProyectoGrado.NO_ENCONTRADO);
                    assertThat(ex.getError().getMessage()).doesNotContain(entregable.toString());
                    assertThat(ex.getMessage()).contains(entregable.toString());
                });
    }
}
