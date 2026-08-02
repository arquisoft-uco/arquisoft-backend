package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarAsesorFichaValidatorTest {

    @Mock
    private AsesorFichaExisteRule asesorFichaExisteRule;

    @InjectMocks
    private CambiarAsesorFichaValidator validator;

    @Test
    void debeDelegarEnLaReglaDeAsesor_cuandoValida() {
        // Arrange
        UUID asesor = UUID.randomUUID();

        // Act
        validator.validar(asesor);

        // Assert
        verify(asesorFichaExisteRule).validar(asesor);
    }
}
