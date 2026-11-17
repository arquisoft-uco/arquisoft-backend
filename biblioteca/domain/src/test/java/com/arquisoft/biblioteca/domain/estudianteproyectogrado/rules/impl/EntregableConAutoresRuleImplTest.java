package com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.biblioteca.domain.estudianteproyectogrado.exception.EntregableSinAutoresException;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutoresEntregable;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableConAutoresRuleImplTest {

    private final EntregableConAutoresRuleImpl rule = new EntregableConAutoresRuleImpl();

    @Test
    void debeLanzarSinAutores_cuandoLaListaEstaVacia() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var autor = new AutorDeEntregable(UtilUUID.generarNuevoUUID(), true);
        var conAutores = new AutoresEntregable(entregable, List.of(autor));
        var sinAutores = new AutoresEntregable(entregable, List.of());

        // Act & Assert
        assertThatCode(() -> rule.validar(conAutores)).doesNotThrowAnyException();
        assertThatThrownBy(() -> rule.validar(sinAutores))
                .isInstanceOfSatisfying(EntregableSinAutoresException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(BibliotecaCodes.EstudianteProyectoGrado.SIN_AUTORES);
                    assertThat(ex.getError().getMessage()).doesNotContain(entregable.toString());
                    assertThat(ex.getMessage()).contains(entregable.toString());
                });
    }
}
