package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak.mapper;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakMapeoRolesMapperTest {

    @Test
    void debeDevolverConjuntoVacio_cuandoElRolNoTieneComposites() {
        // Act / Assert
        assertThat(KeycloakMapeoRolesMapper.toNombres(List.of())).isEmpty();
        assertThat(KeycloakMapeoRolesMapper.toNombres(null)).isEmpty();
    }

    @Test
    void debeDescartarNombresNulosOEnBlanco() {
        // Arrange
        var sinNombre = new HashMap<String, Object>();
        sinNombre.put("name", null);
        var respuesta = new ArrayList<Map<String, Object>>();
        respuesta.add(Map.of("name", "fichas:ver"));
        respuesta.add(Map.of("name", "  "));
        respuesta.add(Map.of("id", "x"));
        respuesta.add(Map.of("name", 7));
        respuesta.add(sinNombre);
        respuesta.add(null);

        // Act
        var nombres = KeycloakMapeoRolesMapper.toNombres(respuesta);

        // Assert
        assertThat(nombres).containsExactly("fichas:ver");
    }

    @Test
    void debeConstruirLaEntidadConElMapaRecibido() {
        // Act
        var entidad = KeycloakMapeoRolesMapper.toEntity(Map.of("asesor", Set.of("fichas:ver"), "vacio", Set.of()));

        // Assert
        assertThat(entidad.clientRolesPorRolRealm()).containsOnlyKeys("asesor", "vacio");
        assertThat(entidad.clientRolesPorRolRealm().get("vacio")).isEmpty();
    }
}
