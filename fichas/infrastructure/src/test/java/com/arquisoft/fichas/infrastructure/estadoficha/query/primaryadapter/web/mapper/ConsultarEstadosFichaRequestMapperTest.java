package com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.infrastructure.security.FichasRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosFichaRequestMapperTest {

    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";

    @ParameterizedTest
    @CsvSource({
            FichasRoles.Realm.ASESOR_FICHA + "," + FichasRoles.Negocio.ASESOR_FICHA,
            FichasRoles.Realm.COORDINADOR + "," + FichasRoles.Negocio.COORDINADOR,
            FichasRoles.Realm.REPRESENTANTE_COMITE + "," + FichasRoles.Negocio.REPRESENTANTE_COMITE
    })
    void debeTraducirAlRolDeNegocio_cuandoElRolRealmEstaEnLaWhitelist(String rolRealm, String rolNegocio) {
        // Arrange
        var jwt = jwtConClaim(Map.of(ROLES, List.of(rolRealm)));

        // Act
        var query = ConsultarEstadosFichaRequestMapper.toQuery(jwt);

        // Assert
        assertThat(query.roles()).containsExactly(rolNegocio);
    }

    @Test
    void debeUnirLosRolesSinDuplicados_cuandoElLlamanteTieneVariosRoles() {
        // Arrange
        var jwt = jwtConClaim(Map.of(ROLES, List.of(
                FichasRoles.Realm.COORDINADOR,
                FichasRoles.Realm.REPRESENTANTE_COMITE,
                FichasRoles.Realm.COORDINADOR)));

        // Act
        var query = ConsultarEstadosFichaRequestMapper.toQuery(jwt);

        // Assert
        assertThat(query.roles()).containsExactlyInAnyOrder(
                FichasRoles.Negocio.COORDINADOR, FichasRoles.Negocio.REPRESENTANTE_COMITE);
    }

    @Test
    void debeDescartarLosRolesDesconocidos_cuandoNoEstanEnLaWhitelist() {
        // Arrange
        var jwt = jwtConClaim(Map.of(ROLES, List.of(
                "estudiante", "administrador", "offline_access", FichasRoles.Realm.ASESOR_FICHA)));

        // Act
        var query = ConsultarEstadosFichaRequestMapper.toQuery(jwt);

        // Assert
        assertThat(query.roles()).containsExactly(FichasRoles.Negocio.ASESOR_FICHA);
    }

    @Test
    void debeRetornarRolesVacios_cuandoElTokenNoTraeRealmAccess() {
        // Arrange
        var jwt = Jwt.withTokenValue("token").header("alg", "none").subject("usuario").build();

        // Act
        var query = ConsultarEstadosFichaRequestMapper.toQuery(jwt);

        // Assert
        assertThat(query.roles()).isEmpty();
    }

    @Test
    void debeIgnorarLoMalformado_cuandoElClaimNoTieneLaFormaEsperada() {
        // Arrange
        var noEsMapa = jwtConClaim("asesor-ficha");
        var rolesNoEsLista = jwtConClaim(Map.of(ROLES, FichasRoles.Realm.ASESOR_FICHA));
        var entradasNoTexto = jwtConClaim(Map.of(ROLES, List.of(42, FichasRoles.Realm.ASESOR_FICHA)));

        // Act
        var deNoEsMapa = ConsultarEstadosFichaRequestMapper.toQuery(noEsMapa);
        var deRolesNoEsLista = ConsultarEstadosFichaRequestMapper.toQuery(rolesNoEsLista);
        var deEntradasNoTexto = ConsultarEstadosFichaRequestMapper.toQuery(entradasNoTexto);

        // Assert
        assertThat(deNoEsMapa.roles()).isEmpty();
        assertThat(deRolesNoEsLista.roles()).isEmpty();
        assertThat(deEntradasNoTexto.roles()).containsExactly(FichasRoles.Negocio.ASESOR_FICHA);
    }

    private static Jwt jwtConClaim(Object realmAccess) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("usuario")
                .claim(REALM_ACCESS, realmAccess)
                .build();
    }
}
