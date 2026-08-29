package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificacionRevisionItemDomainTest {

    @Test
    void debeConstruirModificacion_cuandoDatosValidos() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();

        // Act
        var modificacion = ModificacionRevisionItemDomain.crear(item, "VISUALIZADA", asesorFicha);

        // Assert
        assertThat(modificacion.getItem()).isEqualTo(item);
        assertThat(modificacion.getEstadoRevision()).isEqualTo(EstadoRevision.VISUALIZADA);
        assertThat(modificacion.getAsesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ModificacionRevisionItemDomain.crear(null, "NUEVA", UUID.randomUUID()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.RevisionItem.ITEM);
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoRevisionEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ModificacionRevisionItemDomain.crear(
                UUID.randomUUID(), "   ", UUID.randomUUID()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.RevisionItem.ESTADO_REVISION);
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoRevisionExcedeLongitudMaxima() {
        // Arrange
        String estadoDe51Caracteres = "A".repeat(51);

        // Act & Assert
        assertThatThrownBy(() -> ModificacionRevisionItemDomain.crear(
                UUID.randomUUID(), estadoDe51Caracteres, UUID.randomUUID()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.RevisionItem.ESTADO_REVISION_DEMASIADO_LARGO);
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoRevisionNoPerteneceAlCatalogo() {
        // Act & Assert
        assertThatThrownBy(() -> ModificacionRevisionItemDomain.crear(
                UUID.randomUUID(), "ESTADO_DESCONOCIDO", UUID.randomUUID()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.RevisionItem.ESTADO_REVISION_NO_ENCONTRADO)
                .hasMessageContaining("ESTADO_DESCONOCIDO");
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorFichaNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ModificacionRevisionItemDomain.crear(
                UUID.randomUUID(), "NUEVA", null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.RevisionItem.ASESOR_FICHA);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act & Assert — Notification Pattern: no aborta en el primero
        assertThatThrownBy(() -> ModificacionRevisionItemDomain.crear(null, "", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            FichasCodes.RevisionItem.ITEM_REQUERIDO,
                            FichasCodes.RevisionItem.ESTADO_REVISION_REQUERIDO,
                            FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO);
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            FichasFields.RevisionItem.ITEM,
                            FichasFields.RevisionItem.ESTADO_REVISION,
                            FichasFields.RevisionItem.ASESOR_FICHA);
                });
    }
}
