package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionItemDomainTest {

    @Test
    void debeConstruirRevisionItemConEstadoNueva_cuandoDatosValidos() {
        // Arrange — el asesor la crea; el resto de cambios de estado son del estudiante,
        // así que el estado inicial no se recibe, se fija internamente en 'NUEVA'.
        UUID item = UUID.randomUUID();

        // Act
        RevisionItemDomain revisionItem = RevisionItemDomain.crear(item);

        // Assert
        assertThat(revisionItem.getId()).isNotNull();
        assertThat(revisionItem.getItem()).isEqualTo(item);
        assertThat(revisionItem.getEstadoRevision()).isEqualTo(EstadoRevision.NUEVA);
        assertThat(revisionItem.getFechaCreacion()).isNotNull();
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNulo() {
        // Act & Assert
        assertThatThrownBy(() -> RevisionItemDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.RevisionItem.ITEM);
    }

    @Test
    void debeConstruirRevisionItem_cuandoCrearConEstadoYDatosValidos() {
        // Arrange
        UUID item = UUID.randomUUID();

        // Act
        RevisionItemDomain revisionItem = RevisionItemDomain.crearConEstado(item, "EN_PROGRESO");

        // Assert
        assertThat(revisionItem.getEstadoRevision()).isEqualTo(EstadoRevision.EN_PROGRESO);
    }

    @Test
    void debeLanzarExcepcion_cuandoCrearConEstadoYEstadoRevisionEnBlanco() {
        // Arrange
        UUID item = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> RevisionItemDomain.crearConEstado(item, "   "))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.RevisionItem.ESTADO_REVISION);
    }

    @Test
    void debeLanzarExcepcion_cuandoCrearConEstadoYEstadoRevisionExcedeLongitudMaxima() {
        // Arrange
        UUID item = UUID.randomUUID();
        String estadoDe51Caracteres = "A".repeat(51);

        // Act & Assert
        assertThatThrownBy(() -> RevisionItemDomain.crearConEstado(item, estadoDe51Caracteres))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.RevisionItem.ESTADO_REVISION_DEMASIADO_LARGO);
    }

    @Test
    void debeLanzarExcepcion_cuandoCrearConEstadoYEstadoRevisionNoPerteneceAlCatalogo() {
        // Arrange
        UUID item = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> RevisionItemDomain.crearConEstado(item, "ESTADO_DESCONOCIDO"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.RevisionItem.ESTADO_REVISION_NO_ENCONTRADO)
                .hasMessageContaining("ESTADO_DESCONOCIDO");
    }

    @Test
    void debeAcumularAmbosErrores_cuandoCrearConEstadoYItemYEstadoRevisionSonInvalidos() {
        // Act & Assert — Notification Pattern: una sola excepción con los dos errores
        assertThatThrownBy(() -> RevisionItemDomain.crearConEstado(null, ""))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            FichasCodes.RevisionItem.ITEM_REQUERIDO,
                            FichasCodes.RevisionItem.ESTADO_REVISION_REQUERIDO);
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            FichasFields.RevisionItem.ITEM,
                            FichasFields.RevisionItem.ESTADO_REVISION);
                });
    }
}
