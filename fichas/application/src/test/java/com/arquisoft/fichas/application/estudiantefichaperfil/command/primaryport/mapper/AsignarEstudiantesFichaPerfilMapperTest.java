package com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AsignarEstudiantesFichaPerfilMapperTest {

    @Test
    void debeMapearADomain_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(fichaId, List.of(estudiante));

        // Act
        var relaciones = AsignarEstudiantesFichaPerfilMapper.toDomain(command).getRelaciones();

        // Assert
        assertThat(relaciones).hasSize(1);
        assertThat(relaciones.get(0).getFichaPerfilId()).isEqualTo(fichaId);
        assertThat(relaciones.get(0).getEstudianteId()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoEstudiantesEstaVacia() {
        // Arrange
        var command = new AsignarEstudiantesFichaPerfilCommand(UUID.randomUUID(), List.of());

        // Act & Assert
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> AsignarEstudiantesFichaPerfilMapper.toDomain(command));

        assertThat(excepcion.getValidationResult().tieneErroresDeCampo(FichasFields.EstudianteFichaPerfil.ESTUDIANTES)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoFichaPerfilEsNula() {
        // Arrange
        var command = new AsignarEstudiantesFichaPerfilCommand(null, List.of(UUID.randomUUID()));

        // Act & Assert
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> AsignarEstudiantesFichaPerfilMapper.toDomain(command));

        assertThat(excepcion.getValidationResult().tieneErroresDeCampo(FichasFields.EstudianteFichaPerfil.FICHA_PERFIL)).isTrue();
    }
}
