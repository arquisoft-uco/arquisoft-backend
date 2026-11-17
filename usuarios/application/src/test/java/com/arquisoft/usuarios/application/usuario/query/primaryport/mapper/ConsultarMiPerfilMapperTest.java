package com.arquisoft.usuarios.application.usuario.query.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarMiPerfilQuery;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarMiPerfilMapperTest {

    @Test
    void debeConvertirEnCriteria_cuandoLaQueryEsValida() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var criteria = ConsultarMiPerfilMapper.toCriteria(ConsultarMiPerfilQuery.crear(usuario));

        // Assert
        assertThat(criteria.usuario()).isEqualTo(usuario);
    }
}
