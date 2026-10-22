package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak.KeycloakFuenteMapeoRolesProperties;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis.MapeoRolesProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import com.arquisoft.shared.util.UtilTexto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ValidadorConfiguracionSeguridad {

    private static final String PERFIL_PRODUCCION = "prod";
    private static final String MODO_TEMPLATE = "template";

    // Corre en @PostConstruct, posiblemente antes de que se instale el catalogo de mensajes: sus textos son literales.
    private static final String ERROR_CONFIGURACION_INSEGURA = "Configuración de seguridad inválida: %s";
    private static final String SEPARADOR_PROBLEMAS = "; ";
    private static final String SIN_EMISORES = "ningun emisor habilitado en security.authentication.issuers";
    private static final String ID_EMISOR_INVALIDO = "id de emisor vacio o duplicado: %s";
    private static final String EMISOR_SIN_ISSUER = "el emisor %s no declara issuer";
    private static final String TABLA_SIN_MODO_TEMPLATE =
            "el modo role-table exige security.authorization.resource.path-mode=template";
    private static final String TABLA_SIN_CLAIM_ROLES = "el emisor %s con role-table exige claims.roles";
    private static final String MODO_NONE_EN_PRODUCCION = "el modo de autorizacion none no se permite en produccion";
    private static final String SEGURIDAD_DESHABILITADA_EN_PRODUCCION =
            "security.enabled=false no se permite en produccion";
    private static final String PEP_HTTP_EN_PRODUCCION =
            "security.authorization.pep.allow-insecure-http=true no se permite en produccion";

    private final AppLogger logger;
    private final AutenticacionProperties propiedadesAutenticacion;
    private final PepProperties pep;
    private final MapeoRolesProperties mapeoRoles;
    private final KeycloakFuenteMapeoRolesProperties fuenteMapeoRoles;
    private final RutasPublicas rutasPublicas;
    private final Environment entorno;

    @Value("${security.enabled:true}")
    private boolean seguridadHabilitada;

    @Value("${security.authorization.resource.path-mode:template}")
    private String modoRuta;

    @PostConstruct
    void validar() {
        var problemas = problemas();
        if (!problemas.isEmpty()) {
            throw new InfrastructureException(
                    ERROR_CONFIGURACION_INSEGURA.formatted(String.join(SEPARADOR_PROBLEMAS, problemas)),
                    SeguridadCodes.Autorizacion.CONFIGURACION_INSEGURA);
        }
    }

    List<String> problemas() {
        var problemas = new ArrayList<String>();
        var habilitados = propiedadesAutenticacion.habilitados();
        if (habilitados.isEmpty()) {
            problemas.add(SIN_EMISORES);
        }
        var ids = new HashSet<String>();
        for (var emisor : propiedadesAutenticacion.issuers()) {
            if (UtilTexto.esVacioONulo(emisor.id()) || !ids.add(emisor.id())) {
                problemas.add(ID_EMISOR_INVALIDO.formatted(emisor.id()));
            }
        }
        for (var emisor : habilitados) {
            if (UtilTexto.esVacioONulo(emisor.issuer())) {
                problemas.add(EMISOR_SIN_ISSUER.formatted(emisor.id()));
            }
        }
        var usaPep = habilitados.stream().anyMatch(emisor -> emisor.authorization() == ModoAutorizacion.PEP);
        if (usaPep) {
            problemas.addAll(pep.problemas());
        }
        var usaTabla = habilitados.stream().anyMatch(emisor -> emisor.authorization() == ModoAutorizacion.ROLE_TABLE);
        if (usaTabla && !MODO_TEMPLATE.equalsIgnoreCase(modoRuta)) {
            problemas.add(TABLA_SIN_MODO_TEMPLATE);
        }
        if (usaTabla) {
            habilitados.stream()
                    .filter(emisor -> emisor.authorization() == ModoAutorizacion.ROLE_TABLE)
                    .filter(emisor -> UtilTexto.esVacioONulo(emisor.claims().roles()))
                    .forEach(emisor -> problemas.add(TABLA_SIN_CLAIM_ROLES.formatted(emisor.id())));
            problemas.addAll(mapeoRoles.problemas());
            problemas.addAll(fuenteMapeoRoles.problemas());
        }
        if (entorno.acceptsProfiles(Profiles.of(PERFIL_PRODUCCION))) {
            if (habilitados.stream().anyMatch(emisor -> emisor.authorization() == ModoAutorizacion.NONE)) {
                problemas.add(MODO_NONE_EN_PRODUCCION);
            }
            if (!seguridadHabilitada) {
                problemas.add(SEGURIDAD_DESHABILITADA_EN_PRODUCCION);
            }
            if (pep.allowInsecureHttp()) {
                problemas.add(PEP_HTTP_EN_PRODUCCION);
            }
        }
        return problemas;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registrarConfiguracionAplicada() {
        var emisores = propiedadesAutenticacion.habilitados().stream()
                .map(AutenticacionProperties.Emisor::id)
                .collect(Collectors.joining(","));
        var modos = propiedadesAutenticacion.habilitados().stream()
                .map(emisor -> emisor.id() + "=" + emisor.authorization())
                .collect(Collectors.joining(","));
        logger.info(AutorizacionKey.LOG_CONFIGURACION_AUTORIZACION, emisores, modos, seguridadHabilitada);
        propiedadesAutenticacion.habilitados().stream()
                .filter(emisor -> emisor.audiences().isEmpty())
                .forEach(emisor -> logger.warn(AutorizacionKey.LOG_AUDIENCIA_SIN_VALIDAR, emisor.id()));
    }
}
