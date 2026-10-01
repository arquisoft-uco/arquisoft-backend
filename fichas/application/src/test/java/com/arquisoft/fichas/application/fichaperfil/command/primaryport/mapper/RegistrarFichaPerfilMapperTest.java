package com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistrarFichaPerfilMapperTest {

    @Test
    void debeMapearADomain_cuandoDatosValidos() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        var command = new RegistrarFichaPerfilCommand("Título de prueba", asesor, List.of(UUID.randomUUID()));

        // Act
        RegistroFichaPerfilDomain registro = RegistrarFichaPerfilMapper.toDomain(command);

        // Assert
        assertThat(registro.getFicha().getId()).isNotNull();
        assertThat(registro.getFicha().getTituloProyecto()).isEqualTo("Título de prueba");
        assertThat(registro.getFicha().getAsesorFicha()).isEqualTo(asesor);
        assertThat(registro.getEstadoInicial().getFichaPerfil()).isEqualTo(registro.getFichaPerfil());
        assertThat(registro.getEstudiantes().getFichaPerfil()).isEqualTo(registro.getFichaPerfil());
        assertThat(registro.getEstudiantes().getEstudiantes()).isEqualTo(command.estudiantes());
    }

    @Test
    void debeLanzarDomainValidationException_cuandoNoHayEstudiantes() {
        // Arrange
        var command = new RegistrarFichaPerfilCommand("Título de prueba", UUID.randomUUID(), List.of());

        // Act & Assert
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RegistrarFichaPerfilMapper.toDomain(command));

        assertThat(excepcion.getValidationResult().tieneErroresDeCampo(FichasFields.EstudianteFichaPerfil.ESTUDIANTES)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoTituloEsNulo() {
        // Arrange
        var command = new RegistrarFichaPerfilCommand(null, UUID.randomUUID(), List.of());

        // Act & Assert
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RegistrarFichaPerfilMapper.toDomain(command));

        assertThat(excepcion.getValidationResult().tieneErroresDeCampo(FichasFields.FichaPerfil.TITULO)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoAsesorFichaEsNulo() {
        // Arrange
        var command = new RegistrarFichaPerfilCommand("Título de prueba", null, List.of());

        // Act & Assert
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RegistrarFichaPerfilMapper.toDomain(command));

        assertThat(excepcion.getValidationResult().tieneErroresDeCampo(FichasFields.FichaPerfil.ASESOR_FICHA)).isTrue();
    }
}
