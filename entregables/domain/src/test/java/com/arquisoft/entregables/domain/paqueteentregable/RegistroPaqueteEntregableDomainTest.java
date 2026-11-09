package com.arquisoft.entregables.domain.paqueteentregable;

import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistroPaqueteEntregableDomainTest {

    @Test
    void debeCrear_cuandoLasDosPartesEstanPresentes() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var paquete = PaqueteEntregableDomain.crear(UtilUUID.generarNuevoUUID(), entregable, "bucket",
                "paquetes/x.zip", "p.zip", 10L, Instant.now());
        var artefactos = AgregacionArtefactosEntregableDomain.crear(entregable, List.of());

        // Act
        var registro = RegistroPaqueteEntregableDomain.crear(paquete, artefactos);

        // Assert
        assertThat(registro.getPaquete()).isSameAs(paquete);
        assertThat(registro.getArtefactos()).isSameAs(artefactos);
    }

    @Test
    void debeRechazar_cuandoFaltaUnaParte() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroPaqueteEntregableDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.PAQUETE)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.ARTEFACTOS)).isTrue();
                });
    }
}
