package com.arquisoft.fichas.domain.estadofichaperfil.aggregate;

import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaPerfilDomainTest {

    @Test
    void debeConstruirEntidad_cuandoDatosValidos() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.crear(fichaPerfilId);

        // Assert
        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getId()).isNotNull();
        assertThat(aggregate.getFichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(aggregate.getEstadoFicha()).isEqualTo(EstadoFicha.EN_CONSTRUCCION);
        assertThat(aggregate.getFechaActualizacion()).isNotNull();
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.reconstruir(
                id,
                fichaPerfilId,
                EstadoFicha.EN_CONSTRUCCION,
                java.time.Instant.now()
        );

        // Assert
        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getId()).isEqualTo(id);
        assertThat(aggregate.getFichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(aggregate.getEstadoFicha()).isEqualTo(EstadoFicha.EN_CONSTRUCCION);
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaPerfilIdEsNulo() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> EstadoFichaPerfilDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.EstadoFichaPerfil.FICHA_PERFIL);
    }

    @Test
    void debeAsignarEstadoEnConstruccion_cuandoCrear() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.crear(fichaPerfilId);

        // Assert
        assertThat(aggregate.getEstadoFicha()).isEqualTo(EstadoFicha.EN_CONSTRUCCION);
        assertThat(aggregate.getEstadoFicha().getNombre()).isEqualTo("En Construccion");
    }

    @Test
    void debeGenerarFechaActualizacionAutomatica_cuandoCrear() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.crear(fichaPerfilId);

        // Assert
        assertThat(aggregate.getFechaActualizacion()).isNotNull();
        assertThat(aggregate.getFechaActualizacion()).isBefore(java.time.Instant.now().plusSeconds(1));
    }
}
