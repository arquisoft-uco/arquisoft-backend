package com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate;

import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;

import java.util.List;
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

    @Test
    void debeCrearRelaciones_cuandoListaValidaYLimiteNoExcedido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        List<UUID> estudiantesIds = List.of(estudiante1, estudiante2);

        // Act
        List<EstudianteFichaPerfilAggregate> relaciones = EstudianteFichaPerfilAggregate.crear(
                fichaPerfilId,
                estudiantesIds,
                1L
        );

        // Assert
        assertThat(relaciones).isNotNull();
        assertThat(relaciones).hasSize(2);
        assertThat(relaciones.get(0).getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(relaciones.get(0).getEstudianteId()).isEqualTo(estudiante1);
        assertThat(relaciones.get(1).getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(relaciones.get(1).getEstudianteId()).isEqualTo(estudiante2);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoExistentes2MasNuevos2() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        List<UUID> estudiantesIds = List.of(estudiante1, estudiante2);

        // Act
        Throwable ex = org.assertj.core.api.Assertions.catchThrowable(() ->
                EstudianteFichaPerfilAggregate.crear(fichaPerfilId, estudiantesIds, 2L)
        );

        // Assert
        assertThat(ex)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO_MSG.formatted(
                        FichasMessages.FichaPerfil.ESTUDIANTES_MAX
                ));

        DomainValidationException domainEx = (DomainValidationException) ex;
        assertThat(domainEx.getValidationResult().getErrors())
                .anyMatch(error -> error.errorCode().equals(
                        FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO
                ));
    }

    @Test
    void debePermitirLimiteExacto_cuandoExistentes0MasNuevos3() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        UUID estudiante3 = UUID.randomUUID();
        List<UUID> estudiantesIds = List.of(estudiante1, estudiante2, estudiante3);

        // Act
        List<EstudianteFichaPerfilAggregate> relaciones = EstudianteFichaPerfilAggregate.crear(
                fichaPerfilId,
                estudiantesIds,
                0L
        );

        // Assert
        assertThat(relaciones).isNotNull();
        assertThat(relaciones).hasSize(3);
    }
}
