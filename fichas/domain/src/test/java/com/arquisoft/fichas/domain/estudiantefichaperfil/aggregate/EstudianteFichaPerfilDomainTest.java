package com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

class EstudianteFichaPerfilDomainTest {

    @Test
    void debeConstruirRelacion_cuandoDatosValidos() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        List<EstudianteFichaPerfilDomain> relaciones =
                EstudianteFichaPerfilDomain.crear(fichaPerfilId, List.of(estudianteId));

        // Assert
        assertThat(relaciones).hasSize(1);
        EstudianteFichaPerfilDomain relacion = relaciones.get(0);
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
        List<EstudianteFichaPerfilDomain> relaciones =
                EstudianteFichaPerfilDomain.crear(fichaPerfilId, List.of(estudiante1, estudiante2));

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
        EstudianteFichaPerfilDomain relacion =
                EstudianteFichaPerfilDomain.reconstruir(id, fichaPerfilId, estudianteId);

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
        assertThatThrownBy(() -> EstudianteFichaPerfilDomain.crear(null, List.of(estudianteId)))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoEstudianteIdEsNull() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() ->
                EstudianteFichaPerfilDomain.crear(fichaPerfilId, Arrays.asList((UUID) null)))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoListaEstaVacia() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        Throwable ex = catchThrowable(() -> EstudianteFichaPerfilDomain.crear(fichaPerfilId, List.of()));

        // Assert
        assertThat(ex).isInstanceOf(DomainValidationException.class);
        assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                .anyMatch(error -> error.codigoError().equals(
                        FichasCodes.EstudianteFichaPerfil.ESTUDIANTES_REQUERIDOS));
    }

    @Test
    void debeLanzarDomainValidationException_cuandoListaEsNull() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        assertThatThrownBy(() -> EstudianteFichaPerfilDomain.crear(fichaPerfilId, null))
                .isInstanceOf(DomainValidationException.class);
    }



}
