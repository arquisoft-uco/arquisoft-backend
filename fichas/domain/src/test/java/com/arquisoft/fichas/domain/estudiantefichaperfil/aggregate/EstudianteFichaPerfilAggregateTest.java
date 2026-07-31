package com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate;

import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

class EstudianteFichaPerfilAggregateTest {

    // ─── crear: integridad de la petición ─────────────────────────────────────

    @Test
    void debeConstruirRelacion_cuandoDatosValidos() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        List<EstudianteFichaPerfilAggregate> relaciones =
                EstudianteFichaPerfilAggregate.crear(fichaPerfilId, List.of(estudianteId));

        // Assert
        assertThat(relaciones).hasSize(1);
        EstudianteFichaPerfilAggregate relacion = relaciones.get(0);
        assertThat(relacion.getId()).isNotNull();
        assertThat(relacion.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(relacion.getEstudianteId()).isEqualTo(estudianteId);
    }

    @Test
    void debeCrearRelaciones_cuandoListaTieneVariosEstudiantes() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();

        // Act
        List<EstudianteFichaPerfilAggregate> relaciones =
                EstudianteFichaPerfilAggregate.crear(fichaPerfilId, List.of(estudiante1, estudiante2));

        // Assert
        assertThat(relaciones).hasSize(2);
        assertThat(relaciones.get(0).getEstudianteId()).isEqualTo(estudiante1);
        assertThat(relaciones.get(1).getEstudianteId()).isEqualTo(estudiante2);
        assertThat(relaciones).allMatch(r -> r.getFichaPerfilId().equals(fichaPerfilId));
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        EstudianteFichaPerfilAggregate relacion =
                EstudianteFichaPerfilAggregate.reconstruir(id, fichaPerfilId, estudianteId);

        // Assert
        assertThat(relacion).isNotNull();
        assertThat(relacion.getId()).isEqualTo(id);
        assertThat(relacion.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(relacion.getEstudianteId()).isEqualTo(estudianteId);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoFichaIdEsNull() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> EstudianteFichaPerfilAggregate.crear(null, List.of(estudianteId)))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoEstudianteIdEsNull() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() ->
                EstudianteFichaPerfilAggregate.crear(fichaPerfilId, Arrays.asList((UUID) null)))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoListaEstaVacia() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        Throwable ex = catchThrowable(() -> EstudianteFichaPerfilAggregate.crear(fichaPerfilId, List.of()));

        // Assert
        assertThat(ex).isInstanceOf(DomainValidationException.class);
        assertThat(((DomainValidationException) ex).getValidationResult().getErrors())
                .anyMatch(error -> error.errorCode().equals(
                        FichasMessages.EstudianteFichaPerfil.ESTUDIANTES_REQUERIDOS));
    }

    @Test
    void debeLanzarDomainValidationException_cuandoListaEsNull() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act & Assert — sin NPE: la integridad se evalúa antes de recorrer la colección
        assertThatThrownBy(() -> EstudianteFichaPerfilAggregate.crear(fichaPerfilId, null))
                .isInstanceOf(DomainValidationException.class);
    }

    // ─── validarCupoDisponible: regla de negocio del conjunto ─────────────────

    @Test
    void debeLanzarDomainValidationException_cuandoExistentes2MasNuevos2() {
        // Act
        Throwable ex = catchThrowable(() -> EstudianteFichaPerfilAggregate.validarCupoDisponible(2, 2L));

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
        // Act & Assert
        assertThatCode(() -> EstudianteFichaPerfilAggregate.validarCupoDisponible(3, 0L))
                .doesNotThrowAnyException();
    }

    @Test
    void debePermitirAsignacion_cuandoQuedaCupo() {
        // Act & Assert
        assertThatCode(() -> EstudianteFichaPerfilAggregate.validarCupoDisponible(2, 1L))
                .doesNotThrowAnyException();
    }
}
