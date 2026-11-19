package com.arquisoft.shared.catalogoparametros.application.command.result.mapper;

import com.arquisoft.shared.catalogoparametros.application.command.result.RefrescoParametrosCatalogoResult;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity.ParametrosCatalogoEntity;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RefrescoParametrosCatalogoResultMapperTest {

    @Test
    void debeContarLosValores_cuandoRefrescado() {
        // Arrange
        var entity = new ParametrosCatalogoEntity(Map.of("A", "1", "B", "2"));

        // Act
        var resultado = RefrescoParametrosCatalogoResultMapper.toResultRefrescado(entity);

        // Assert
        assertThat(resultado.cantidad()).isEqualTo(2);
    }

    @Test
    void debeCrearUnaVariantePorFabrica() {
        // Arrange / Act / Assert
        assertThat(RefrescoParametrosCatalogoResultMapper.toResultConservado("estado-503"))
                .isEqualTo(new RefrescoParametrosCatalogoResult.Conservado("estado-503"));
        assertThat(RefrescoParametrosCatalogoResultMapper.toResultRechazado(401))
                .isEqualTo(new RefrescoParametrosCatalogoResult.Rechazado(401));
        assertThat(RefrescoParametrosCatalogoResultMapper.toResultSuspendido())
                .isInstanceOf(RefrescoParametrosCatalogoResult.Suspendido.class);
        assertThat(RefrescoParametrosCatalogoResultMapper.toResultSinConfigurar())
                .isInstanceOf(RefrescoParametrosCatalogoResult.SinConfigurar.class);
    }
}
