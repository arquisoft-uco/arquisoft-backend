package com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CambiarAsesorFichaMapperTest {

    @Test
    void debeMapearADomain_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        var command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        // Act
        CambioAsesorFichaDomain cambio = CambiarAsesorFichaMapper.toDomain(command);

        // Assert
        assertThat(cambio.getFichaPerfil()).isEqualTo(fichaId);
        assertThat(cambio.getNuevoAsesorFicha()).isEqualTo(nuevoAsesorId);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoNuevoAsesorEsNulo() {
        // Arrange
        var command = new CambiarAsesorFichaCommand(UUID.randomUUID(), null);

        // Act & Assert
        DomainValidationException excepcion = assertThrows(DomainValidationException.class, () -> CambiarAsesorFichaMapper.toDomain(command));

        assertThat(excepcion.getValidationResult().tieneErroresDeCampo(FichasFields.FichaPerfil.ASESOR_FICHA)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoFichaPerfilEsNula() {
        // Arrange
        var command = new CambiarAsesorFichaCommand(null, UUID.randomUUID());

        // Act & Assert
        DomainValidationException excepcion = assertThrows(DomainValidationException.class, () -> CambiarAsesorFichaMapper.toDomain(command));

        assertThat(excepcion.getValidationResult().tieneErroresDeCampo(FichasFields.FichaPerfil.ID)).isTrue();
    }
}
