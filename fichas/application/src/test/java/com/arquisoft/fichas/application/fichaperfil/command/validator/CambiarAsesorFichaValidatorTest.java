package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.CambiarAsesorFichaValidatorImpl;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.AsesorFichaComparacion;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class CambiarAsesorFichaValidatorTest {

    @Mock
    private FichaPerfilExisteRule fichaPerfilExisteRule;

    @Mock
    private AsesorFichaExisteRule asesorFichaExisteRule;

    @Mock
    private EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

    @Mock
    private AsesorFichaDiferenteRule asesorFichaDiferenteRule;

    @InjectMocks
    private CambiarAsesorFichaValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoValida() {
        // Arrange — existencia primero, reglas de negocio despues
        UUID asesorActual = UUID.randomUUID();
        UUID nuevoAsesor = UUID.randomUUID();
        var ficha = FichaPerfilDomain.crear("Titulo de prueba", asesorActual);
        var cambio = CambioAsesorFichaDomain.crear(ficha.getId(), nuevoAsesor);
        var estadoActual = Optional.of(EstadoFicha.EN_CONSTRUCCION);

        // Act
        validator.validar(cambio, Optional.of(ficha), true, estadoActual);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteRule, asesorFichaExisteRule,
                estadoFichaPerfilEnTerminalRule, asesorFichaDiferenteRule);
        inOrder.verify(fichaPerfilExisteRule)
                .validar(new ExistenciaFichaPerfil(cambio.getFichaPerfil(), true));
        inOrder.verify(asesorFichaExisteRule)
                .validar(new ExistenciaAsesorFicha(nuevoAsesor, true));
        inOrder.verify(estadoFichaPerfilEnTerminalRule)
                .validar(new EstadoActualFicha(cambio.getFichaPerfil(), estadoActual));
        inOrder.verify(asesorFichaDiferenteRule)
                .validar(new AsesorFichaComparacion(nuevoAsesor, asesorActual));
    }

    @Test
    void debeTrasladarLaAusenciaDeFicha_cuandoLaFichaNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesor = UUID.randomUUID();
        var cambio = CambioAsesorFichaDomain.crear(fichaId, nuevoAsesor);

        // Act — la regla de existencia es un mock que no lanza, pero recibe existe=false
        validator.validar(cambio, Optional.empty(), false, Optional.empty());

        // Assert
        inOrder(fichaPerfilExisteRule).verify(fichaPerfilExisteRule)
                .validar(new ExistenciaFichaPerfil(fichaId, false));
    }
}
