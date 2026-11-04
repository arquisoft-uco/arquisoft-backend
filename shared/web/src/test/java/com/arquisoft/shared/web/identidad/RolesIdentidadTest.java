package com.arquisoft.shared.web.identidad;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RolesIdentidadTest {

    private static final Map<String, String> ROL_POR_ROL_IDENTIDAD = Map.of(
            "coordinador", "COORDINADOR",
            "asesor-ficha", "ASESOR_FICHA");

    @Test
    void debeTraducirLosRolesConocidos_cuandoLaIdentidadLosTrae() {
        // Arrange
        var identidad = identidadConRoles(List.of("asesor-ficha", "coordinador"));

        // Act
        var roles = RolesIdentidad.traducir(identidad, ROL_POR_ROL_IDENTIDAD);

        // Assert
        assertThat(roles).containsExactly("ASESOR_FICHA", "COORDINADOR");
    }

    @Test
    void debeDescartarLosRolesSinTraduccion_cuandoNoEstanEnElMapa() {
        // Arrange
        var identidad = identidadConRoles(List.of("offline_access", "estudiante", "coordinador"));

        // Act
        var roles = RolesIdentidad.traducir(identidad, ROL_POR_ROL_IDENTIDAD);

        // Assert
        assertThat(roles).containsExactly("COORDINADOR");
    }

    @Test
    void debeQuitarDuplicados_cuandoVariosRolesTraducenAlMismo() {
        // Arrange
        var identidad = identidadConRoles(List.of("coordinador", "coordinador-sede", "coordinador"));
        var rolPorRolIdentidad = Map.of("coordinador", "COORDINADOR", "coordinador-sede", "COORDINADOR");

        // Act
        var roles = RolesIdentidad.traducir(identidad, rolPorRolIdentidad);

        // Assert
        assertThat(roles).containsExactly("COORDINADOR");
    }

    @Test
    void debeRetornarVacio_cuandoLaIdentidadNoTraeRoles() {
        // Arrange
        var identidad = identidadConRoles(null);

        // Act
        var roles = RolesIdentidad.traducir(identidad, ROL_POR_ROL_IDENTIDAD);

        // Assert
        assertThat(roles).isEmpty();
    }

    private static IdentidadActual identidadConRoles(List<String> roles) {
        return new IdentidadActual(UtilUUID.generarNuevoUUID(), "a@uco.edu.co", "a", roles, "jti", null);
    }
}
