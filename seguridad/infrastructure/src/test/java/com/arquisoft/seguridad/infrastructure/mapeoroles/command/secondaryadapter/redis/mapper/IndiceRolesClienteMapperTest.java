package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis.mapper;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IndiceRolesClienteMapperTest {

    @Test
    void debeInvertirElMapa() {
        // Arrange
        var mapeo = new MapeoRolesEntity(Map.of("asesor", Set.of("fichas:ver", "fichas:editar")));

        // Act
        var indice = IndiceRolesClienteMapper.toIndice(mapeo);

        // Assert
        assertThat(indice).containsOnlyKeys("fichas:ver", "fichas:editar");
        assertThat(indice.get("fichas:ver")).containsExactly("asesor");
        assertThat(indice.get("fichas:editar")).containsExactly("asesor");
    }

    @Test
    void debeUnirLosRolesDeVariosRealmRolesPorClientRole() {
        // Arrange
        var mapeo = new MapeoRolesEntity(Map.of(
                "asesor", Set.of("fichas:ver"),
                "estudiante", Set.of("fichas:ver", "fichas:crear")));

        // Act
        var indice = IndiceRolesClienteMapper.toIndice(mapeo);

        // Assert
        assertThat(indice.get("fichas:ver")).containsExactlyInAnyOrder("asesor", "estudiante");
        assertThat(indice.get("fichas:crear")).containsExactly("estudiante");
    }

    @Test
    void debeOmitirRolesSinClientRoles() {
        // Arrange
        var mapeo = new MapeoRolesEntity(Map.of("vacio", Set.of(), "asesor", Set.of("fichas:ver")));

        // Act
        var indice = IndiceRolesClienteMapper.toIndice(mapeo);

        // Assert
        assertThat(indice).containsOnlyKeys("fichas:ver");
        assertThat(indice.values()).noneMatch(roles -> roles.contains("vacio"));
    }
}
