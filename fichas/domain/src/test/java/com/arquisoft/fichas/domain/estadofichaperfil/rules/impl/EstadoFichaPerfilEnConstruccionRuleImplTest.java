package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoEnConstruccionException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaPerfilEnConstruccionRuleImplTest {

    private final EstadoFichaPerfilEnConstruccionRuleImpl regla = new EstadoFichaPerfilEnConstruccionRuleImpl();

    @Test
    void debePasar_cuandoLaFichaEstaEnConstruccion() {
        // Arrange
        var estado = new EstadoActualFicha(UtilUUID.generarNuevoUUID(), EstadoFicha.EN_CONSTRUCCION);

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {
            "DISPONIBLE_PARA_EVALUACION", "DESCARTADA", "APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzar_cuandoLaFichaNoEstaEnConstruccion(EstadoFicha noPermitido) {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var estado = new EstadoActualFicha(fichaPerfil, noPermitido);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOfSatisfying(FichaPerfilNoEnConstruccionException.class, e -> {
                    assertThat(e.getCodigoError()).isEqualTo(FichasCodes.EstadoFichaPerfil.NO_EN_CONSTRUCCION);
                    assertThat(e.getMessage()).contains(noPermitido.getNombre());
                    assertThat(e.getMessage()).doesNotContain(fichaPerfil.toString());
                });
    }

    @Test
    void debeLanzar_cuandoLaFichaNoTieneEstado() {
        // Arrange
        var estado = new EstadoActualFicha(UtilUUID.generarNuevoUUID(), EstadoFicha.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(FichaPerfilNoEnConstruccionException.class);
    }
}
