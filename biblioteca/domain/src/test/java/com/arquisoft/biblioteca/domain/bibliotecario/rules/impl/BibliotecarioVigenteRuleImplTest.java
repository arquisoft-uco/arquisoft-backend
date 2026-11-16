package com.arquisoft.biblioteca.domain.bibliotecario.rules.impl;

import com.arquisoft.biblioteca.domain.bibliotecario.exception.BibliotecarioNoVigenteException;
import com.arquisoft.biblioteca.domain.bibliotecario.model.VigenciaBibliotecario;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BibliotecarioVigenteRuleImplTest {

    private final BibliotecarioVigenteRuleImpl rule = new BibliotecarioVigenteRuleImpl();

    @Test
    void debePasar_cuandoElBibliotecarioExisteYNoEstaEliminado() {
        // Arrange
        var vigencia = new VigenciaBibliotecario(UtilUUID.generarNuevoUUID(), true, false);

        // Act & Assert
        assertThatCode(() -> rule.validar(vigencia)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void debeLanzarNoVigente_cuandoEstaAusenteOEliminado(boolean existe, boolean eliminado) {
        // Arrange
        var bibliotecario = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaBibliotecario(bibliotecario, existe, eliminado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vigencia))
                .isInstanceOfSatisfying(BibliotecarioNoVigenteException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(BibliotecaCodes.Bibliotecario.NO_VIGENTE);
                    assertThat(ex.getError().getMessage()).doesNotContain(bibliotecario.toString());
                    assertThat(ex.getMessage()).contains(bibliotecario.toString());
                });
    }
}
