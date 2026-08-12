package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.AgregarItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.AgregacionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.DisponibilidadTipoItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemTipoNoDuplicadoRule;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class AgregarItemFichaPerfilValidatorTest {

    @Mock
    private FichaPerfilExisteRule fichaPerfilExisteRule;

    @Mock
    private ItemFichaPropiaRule itemFichaPropiaRule;

    @Mock
    private ItemTipoNoDuplicadoRule itemTipoNoDuplicadoRule;

    @InjectMocks
    private AgregarItemFichaPerfilValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoValida() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var agregacion = AgregacionItemFichaPerfilDomain.crear(
                fichaPerfil, TipoItem.OBJETIVO_GENERAL.getId(), "Contenido del item", estudiante);

        // Act
        validator.validar(agregacion.getItem(), estudiante, true, true, false);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteRule, itemFichaPropiaRule, itemTipoNoDuplicadoRule);
        inOrder.verify(fichaPerfilExisteRule).validar(new ExistenciaFichaPerfil(fichaPerfil, true));
        inOrder.verify(itemFichaPropiaRule).validar(new PropiedadFicha(fichaPerfil, estudiante, true));
        inOrder.verify(itemTipoNoDuplicadoRule)
                .validar(new DisponibilidadTipoItem(TipoItem.OBJETIVO_GENERAL, false));
    }

    @Test
    void debeTrasladarLosDatosConsultados_cuandoNoEsPropietarioYElTipoYaExiste() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var agregacion = AgregacionItemFichaPerfilDomain.crear(
                fichaPerfil, TipoItem.OBJETIVO_GENERAL.getId(), "Contenido del item", estudiante);

        // Act
        validator.validar(agregacion.getItem(), estudiante, true, false, true);

        // Assert
        InOrder inOrder = inOrder(itemFichaPropiaRule, itemTipoNoDuplicadoRule);
        inOrder.verify(itemFichaPropiaRule).validar(new PropiedadFicha(fichaPerfil, estudiante, false));
        inOrder.verify(itemTipoNoDuplicadoRule)
                .validar(new DisponibilidadTipoItem(TipoItem.OBJETIVO_GENERAL, true));
    }
}
