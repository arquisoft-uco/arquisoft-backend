package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.command.finder.CategoriaItemCuantitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.NombreItemCuantitativoAsesorPorCategoriaExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity.ItemCuantitativoAsesorEntity;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.RegistrarItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.CategoriaItemCuantitativoAsesorNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarItemCuantitativoAsesorUseCaseImplTest {

    @Mock
    private ItemCuantitativoAsesorOutputPort outputPort;

    @Mock
    private CategoriaItemCuantitativoAsesorExisteFinder categoriaExisteFinder;

    @Mock
    private NombreItemCuantitativoAsesorPorCategoriaExisteFinder nombreExisteFinder;

    @Mock
    private RegistrarItemCuantitativoAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarItemCuantitativoAsesorUseCaseImpl useCase;

    @Test
    void debeRegistrarYRetornarId_cuandoValidacionPasa() {
        // Arrange
        var item = itemValido();
        when(categoriaExisteFinder.obtener(item.getCategoria())).thenReturn(true);
        when(nombreExisteFinder.obtener(item)).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(item);

        // Assert
        assertThat(resultado).isEqualTo(item.getId());
        var orden = inOrder(categoriaExisteFinder, nombreExisteFinder, validator, outputPort, logger);
        orden.verify(categoriaExisteFinder, times(1)).obtener(item.getCategoria());
        orden.verify(nombreExisteFinder, times(1)).obtener(item);
        orden.verify(validator).validar(item, true, false);
        orden.verify(outputPort).registrar(new ItemCuantitativoAsesorEntity(
                item.getId(), item.getNombre(), item.getDescripcion(), item.getCategoria(), item.getValor()));
        orden.verify(logger).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    @Test
    void noDebeRegistrarNiCerrarElLog_cuandoElValidatorRechaza() {
        // Arrange
        var item = itemValido();
        when(categoriaExisteFinder.obtener(item.getCategoria())).thenReturn(false);
        when(nombreExisteFinder.obtener(item)).thenReturn(false);
        doThrow(new CategoriaItemCuantitativoAsesorNoEncontradaException(item.getCategoria()))
                .when(validator).validar(item, false, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(item))
                .isInstanceOf(CategoriaItemCuantitativoAsesorNoEncontradaException.class);
        verify(outputPort, never()).registrar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    private static ItemCuantitativoAsesorDomain itemValido() {
        return ItemCuantitativoAsesorDomain.crear("Avance", "Porcentaje", UtilUUID.generarNuevoUUID(), 80);
    }
}
