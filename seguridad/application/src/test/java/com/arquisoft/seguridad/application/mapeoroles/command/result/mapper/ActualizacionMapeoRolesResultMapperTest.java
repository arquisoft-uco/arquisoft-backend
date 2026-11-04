package com.arquisoft.seguridad.application.mapeoroles.command.result.mapper;

import com.arquisoft.seguridad.application.mapeoroles.command.result.ActualizacionMapeoRolesResult;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ActualizacionMapeoRolesResultMapperTest {

    @Test
    void debeContarClientRolesDistintos_cuandoSeMapeaActualizada() {
        // Arrange
        var mapeo = new MapeoRolesEntity(Map.of(
                "asesor", Set.of("a", "b"),
                "estudiante", Set.of("b", "c"),
                "vacio", Set.of()));

        // Act
        var resultado = ActualizacionMapeoRolesResultMapper.toResultActualizada(mapeo);

        // Assert
        assertThat(resultado.rolesRealm()).isEqualTo(3);
        assertThat(resultado.rolesCliente()).isEqualTo(3);
    }

    @Test
    void debeConstruirUnaVariantePorFabrica() {
        // Act / Assert
        assertThat(ActualizacionMapeoRolesResultMapper.toResultVigente())
                .isInstanceOf(ActualizacionMapeoRolesResult.Vigente.class);
        assertThat(ActualizacionMapeoRolesResultMapper.toResultOmitida())
                .isInstanceOf(ActualizacionMapeoRolesResult.Omitida.class);
        assertThat(ActualizacionMapeoRolesResultMapper.toResultFallida("503"))
                .isEqualTo(new ActualizacionMapeoRolesResult.Fallida("503"));
    }
}
