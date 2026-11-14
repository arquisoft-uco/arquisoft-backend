package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteArmadoDesactualizadoException;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfirmarGeneracionEntregableProyectoGradoValidatorImplTest {

    private final ConfirmarGeneracionEntregableProyectoGradoValidatorImpl validator =
            new ConfirmarGeneracionEntregableProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), "Proyecto", EstadoProyecto.EN_PROCESO, null);

    @Test
    void debePasar_cuandoLaVersionYElConjuntoNoCambiaron() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();
        var b = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(proyecto, 2, 2, Set.of(a, b), Set.of(b, a)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDesactualizado_cuandoCambioElConjuntoDeVersionesDeArtefacto() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(proyecto, 2, 2, Set.of(a), Set.of(UtilUUID.generarNuevoUUID())))
                .isInstanceOf(PaqueteArmadoDesactualizadoException.class);
    }

    @Test
    void debeLanzarDesactualizado_cuandoCambioLaVersionDelEntregable() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(proyecto, 2, 3, Set.of(a), Set.of(a)))
                .isInstanceOf(PaqueteArmadoDesactualizadoException.class);
    }
}
