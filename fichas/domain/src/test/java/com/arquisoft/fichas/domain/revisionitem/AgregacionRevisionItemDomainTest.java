package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregacionRevisionItemDomainTest {

    @Test
    void debeConstruirAgregacion_cuandoDatosValidos() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();
        var revisionItem = RevisionItemDomain.crear(item);

        // Act
        var agregacion = AgregacionRevisionItemDomain.crear(revisionItem, asesorFicha);

        // Assert
        assertThat(agregacion.getRevisionItem()).isEqualTo(revisionItem);
        assertThat(agregacion.getItem()).isEqualTo(item);
        assertThat(agregacion.getEstadoRevision()).isEqualTo(EstadoRevision.NUEVA);
        assertThat(agregacion.getAsesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeLanzarExcepcion_cuandoRevisionItemNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionRevisionItemDomain.crear(null, UUID.randomUUID()))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorFichaNulo() {
        // Arrange
        var revisionItem = RevisionItemDomain.crear(UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> AgregacionRevisionItemDomain.crear(revisionItem, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.RevisionItem.ASESOR_FICHA);
    }
}
