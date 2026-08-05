package com.arquisoft.fichas.application.estadofichaperfil.command.validator;

import com.arquisoft.fichas.application.estadofichaperfil.command.validator.impl.AsignarEstadoInicialFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsignarEstadoInicialFichaPerfilValidatorTest {

    @Mock
    private FichaPerfilExisteRule fichaPerfilExisteRule;

    @InjectMocks
    private AsignarEstadoInicialFichaPerfilValidatorImpl validator;

    @Test
    void debeValidarQueLaFichaExiste_cuandoValida() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act
        validator.validar(fichaPerfil);

        // Assert
        verify(fichaPerfilExisteRule).validar(fichaPerfil);
    }
}
