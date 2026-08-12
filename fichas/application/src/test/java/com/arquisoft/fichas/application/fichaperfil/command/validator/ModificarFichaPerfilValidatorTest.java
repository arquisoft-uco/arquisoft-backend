package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.ModificarFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.DisponibilidadTituloFicha;
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
class ModificarFichaPerfilValidatorTest {

    @Mock
    private EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;

    @Mock
    private FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule;

    @InjectMocks
    private ModificarFichaPerfilValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoValida() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var modificacion = ModificacionFichaPerfilDomain.crear(fichaId, "Titulo nuevo", estudiante);

        // Act
        validator.validar(modificacion, true, false);

        // Assert
        InOrder inOrder = inOrder(estudiantePropietarioFichaRule, fichaPerfilTituloUnicoRule);
        inOrder.verify(estudiantePropietarioFichaRule)
                .validar(new PropiedadFicha(fichaId, estudiante, true));
        inOrder.verify(fichaPerfilTituloUnicoRule)
                .validar(new DisponibilidadTituloFicha("Titulo nuevo", false));
    }

    @Test
    void debeTrasladarLosDatosConsultados_cuandoNoEsPropietarioYElTituloEstaTomado() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var modificacion = ModificacionFichaPerfilDomain.crear(fichaId, "Titulo tomado", estudiante);

        // Act
        validator.validar(modificacion, false, true);

        // Assert
        InOrder inOrder = inOrder(estudiantePropietarioFichaRule, fichaPerfilTituloUnicoRule);
        inOrder.verify(estudiantePropietarioFichaRule)
                .validar(new PropiedadFicha(fichaId, estudiante, false));
        inOrder.verify(fichaPerfilTituloUnicoRule)
                .validar(new DisponibilidadTituloFicha("Titulo tomado", true));
    }
}
