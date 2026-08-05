package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.AgregarItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ItemTipoCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemTipoNoDuplicadoRule;
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
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var item = ItemFichaPerfilDomain.crear(ficha, "OBJETIVO_GENERAL", "Contenido de prueba");

        // Act
        validator.validar(item, estudiante);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteRule, itemFichaPropiaRule, itemTipoNoDuplicadoRule);
        inOrder.verify(fichaPerfilExisteRule).validar(ficha);
        inOrder.verify(itemFichaPropiaRule).validar(new PropietarioFichaCriteria(ficha, estudiante));
        inOrder.verify(itemTipoNoDuplicadoRule)
                .validar(new ItemTipoCriteria(ficha, item.getTipoItem().getId()));
    }
}
