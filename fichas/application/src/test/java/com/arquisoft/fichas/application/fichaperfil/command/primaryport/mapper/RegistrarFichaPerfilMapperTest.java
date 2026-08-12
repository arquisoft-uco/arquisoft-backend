package com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarFichaPerfilMapperTest {

    @Test
    void debeMapearADomain_cuandoDatosValidos() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        var command = new RegistrarFichaPerfilCommand("Título de prueba", asesor, List.of(UUID.randomUUID()));

        // Act
        FichaPerfilDomain ficha = RegistrarFichaPerfilMapper.toDomain(command);

        // Assert
        assertThat(ficha.getId()).isNotNull();
        assertThat(ficha.getTituloProyecto()).isEqualTo("Título de prueba");
        assertThat(ficha.getAsesorFicha()).isEqualTo(asesor);
    }

    @Test
    void debeIgnorarLosEstudiantes_cuandoMapeaADomain() {
        // Arrange — estudiantes no es un dato de FichaPerfilDomain, el mapper no debe fallar por su
        // ausencia ni filtrarlo hacia el agregado.
        var command = new RegistrarFichaPerfilCommand("Título de prueba", UUID.randomUUID(), List.of());

        // Act & Assert
        assertThat(RegistrarFichaPerfilMapper.toDomain(command)).isNotNull();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoTituloEsNulo() {
        // Arrange
        var command = new RegistrarFichaPerfilCommand(null, UUID.randomUUID(), List.of());

        // Act & Assert
        assertThatThrownBy(() -> RegistrarFichaPerfilMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.TITULO);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoAsesorFichaEsNulo() {
        // Arrange
        var command = new RegistrarFichaPerfilCommand("Título de prueba", null, List.of());

        // Act & Assert
        assertThatThrownBy(() -> RegistrarFichaPerfilMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.ASESOR_FICHA);
    }
}
