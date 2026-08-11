package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.RegistrarFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilValidatorTest {

    @Mock
    private AsesorFichaExisteRule asesorFichaExisteRule;

    @Mock
    private FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule;

    @InjectMocks
    private RegistrarFichaPerfilValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoValida() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        var ficha = FichaPerfilDomain.crear("Titulo de prueba", asesor);

        // Act
        validator.validar(ficha);

        // Assert
        InOrder inOrder = inOrder(asesorFichaExisteRule, fichaPerfilTituloUnicoRule);
        inOrder.verify(asesorFichaExisteRule).validar(asesor);
        inOrder.verify(fichaPerfilTituloUnicoRule).validar(ficha.getTituloProyecto());
    }
}
