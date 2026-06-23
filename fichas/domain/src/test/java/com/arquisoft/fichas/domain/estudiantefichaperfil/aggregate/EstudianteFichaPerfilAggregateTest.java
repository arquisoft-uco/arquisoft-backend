package com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate;

import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteFichaPerfilAggregateTest {

    @Test
    void debeConstruirRelacion_cuandoDatosValidos() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        EstudianteFichaPerfilAggregate relacion = EstudianteFichaPerfilAggregate.crear(fichaPerfilId, estudianteId);

        // Assert
        assertThat(relacion).isNotNull();
        assertThat(relacion.getId()).isNotNull();
        assertThat(relacion.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(relacion.getEstudianteId()).isEqualTo(estudianteId);
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        EstudianteFichaPerfilAggregate relacion = EstudianteFichaPerfilAggregate.reconstruir(id, fichaPerfilId, estudianteId);

        // Assert
        assertThat(relacion).isNotNull();
        assertThat(relacion.getId()).isEqualTo(id);
        assertThat(relacion.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(relacion.getEstudianteId()).isEqualTo(estudianteId);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoFichaIdEsNull() {
        // Arrange
        UUID fichaPerfilId = null;
        UUID estudianteId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> EstudianteFichaPerfilAggregate.crear(fichaPerfilId, estudianteId))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoEstudianteIdEsNull() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = null;

        // Act & Assert
        assertThatThrownBy(() -> EstudianteFichaPerfilAggregate.crear(fichaPerfilId, estudianteId))
                .isInstanceOf(DomainValidationException.class);
    }
}
