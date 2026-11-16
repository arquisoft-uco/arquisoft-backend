package com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.biblioteca.domain.estudianteproyectogrado.exception.AutorEntregableNoVigenteException;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutoresEntregable;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutoresEntregableVigentesRuleImplTest {

    private final AutoresEntregableVigentesRuleImpl rule = new AutoresEntregableVigentesRuleImpl();

    @Test
    void debeLanzarNoVigenteConLosIdsEnElDetalle_cuandoAlgunAutorNoEstaVigente() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var ausente = UtilUUID.generarNuevoUUID();
        var todosVigentes = new AutoresEntregable(entregable, List.of(new AutorDeEntregable(vigente, true)));
        var conAusente = new AutoresEntregable(entregable,
                List.of(new AutorDeEntregable(vigente, true), new AutorDeEntregable(ausente, false)));

        // Act & Assert
        assertThatCode(() -> rule.validar(todosVigentes)).doesNotThrowAnyException();
        assertThatThrownBy(() -> rule.validar(conAusente))
                .isInstanceOfSatisfying(AutorEntregableNoVigenteException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(BibliotecaCodes.EstudianteProyectoGrado.AUTOR_NO_VIGENTE);
                    assertThat(ex.getError().getMessage())
                            .doesNotContain(entregable.toString())
                            .doesNotContain(ausente.toString());
                    assertThat(ex.getMessage()).contains(entregable.toString()).contains(ausente.toString())
                            .doesNotContain(vigente.toString());
                });
    }
}
