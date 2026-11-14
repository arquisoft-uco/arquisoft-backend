package com.arquisoft.entregables.domain.artefactoentregable;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregacionArtefactosEntregableDomainTest {

    @Test
    void debeCrearConLaFoto_cuandoHayEntregableYArtefactos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var foto = ArtefactoEntregableDomain.crear(entregable, "01-Informe/v3-informe.pdf",
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 3, "Informe",
                new ArtefactoEntregableDomain.DatosRepositorioArtefacto(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo"));

        // Act
        var agregacion = AgregacionArtefactosEntregableDomain.crear(entregable, List.of(foto));

        // Assert
        assertThat(agregacion.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(agregacion.getArtefactos()).containsExactly(foto);
    }

    @Test
    void debeQuedarConListaVaciaYRechazarSinEntregable_cuandoFaltanDatos() {
        // Act
        var agregacion = AgregacionArtefactosEntregableDomain.crear(UtilUUID.generarNuevoUUID(), null);

        // Assert
        assertThat(agregacion.getArtefactos()).isEmpty();
        assertThatThrownBy(() -> AgregacionArtefactosEntregableDomain.crear(null, List.of()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult()
                        .tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.ENTREGABLE_PROYECTO_GRADO)).isTrue());
    }
}
