package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteArmadoDesactualizadoException;
import com.arquisoft.entregables.domain.paqueteentregable.model.VigenciaPaqueteArmado;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaqueteArmadoVigenteRuleImplTest {

    private final PaqueteArmadoVigenteRuleImpl rule = new PaqueteArmadoVigenteRuleImpl();

    @Test
    void debePasar_cuandoVersionYConjuntoCoinciden() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();
        var b = UtilUUID.generarNuevoUUID();
        var vigencia = vigencia(2, 2, Set.of(a, b), Set.of(b, a));

        // Act & Assert
        assertThatCode(() -> rule.validar(vigencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoCambioLaVersionDelEntregable() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();
        var vigencia = vigencia(2, 3, Set.of(a), Set.of(a));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vigencia))
                .isInstanceOfSatisfying(PaqueteArmadoDesactualizadoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.PaqueteEntregable.DESACTUALIZADO);
                    assertThat(ex.getError().getMessage())
                            .contains("Proyecto")
                            .doesNotContain(vigencia.proyectoGrado().toString());
                    assertThat(ex.getMessage()).contains(vigencia.proyectoGrado().toString());
                });
    }

    @Test
    void debeLanzar_cuandoUnArtefactoTieneOtraVersion() {
        // Arrange
        var vigencia = vigencia(1, 1, Set.of(UtilUUID.generarNuevoUUID()), Set.of(UtilUUID.generarNuevoUUID()));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vigencia)).isInstanceOf(PaqueteArmadoDesactualizadoException.class);
    }

    @Test
    void debeLanzar_cuandoUnArtefactoSeOmitioYFaltaEnLosActuales() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();
        var b = UtilUUID.generarNuevoUUID();
        var vigencia = vigencia(1, 1, Set.of(a, b), Set.of(a));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vigencia)).isInstanceOf(PaqueteArmadoDesactualizadoException.class);
    }

    @Test
    void debeLanzar_cuandoUnArtefactoSeReactivoYSobraEnLosActuales() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();
        var b = UtilUUID.generarNuevoUUID();
        var vigencia = vigencia(1, 1, Set.of(a), Set.of(a, b));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vigencia)).isInstanceOf(PaqueteArmadoDesactualizadoException.class);
    }

    private VigenciaPaqueteArmado vigencia(int empaquetada, int actual, Set<UUID> empaquetadas, Set<UUID> actuales) {
        return new VigenciaPaqueteArmado(
                UtilUUID.generarNuevoUUID(), "Proyecto", empaquetada, actual, empaquetadas, actuales);
    }
}
