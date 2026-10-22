package com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MapeoRolesEntityTest {

    @Test
    void debeCopiarElMapa_cuandoSeConstruye() {
        // Arrange
        var interno = new HashSet<>(Set.of("a"));
        var original = new HashMap<String, Set<String>>();
        original.put("asesor", interno);

        // Act
        var entidad = new MapeoRolesEntity(original);
        original.put("otro", Set.of("x"));
        interno.add("b");

        // Assert
        assertThat(entidad.clientRolesPorRolRealm()).containsOnlyKeys("asesor");
        assertThat(entidad.clientRolesPorRolRealm().get("asesor")).containsExactly("a");
        assertThatThrownBy(() -> entidad.clientRolesPorRolRealm().put("z", Set.of()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void debeQuedarVacio_cuandoElMapaEsNulo() {
        // Act / Assert
        assertThat(new MapeoRolesEntity(null).clientRolesPorRolRealm()).isEmpty();
    }
}
