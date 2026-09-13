package com.arquisoft.shared.message.constant;

import java.util.Set;

/**
 * Catálogo quemado de realm roles de Keycloak (kebab-case), tomado del enum {@code UsuarioRole}
 * previo a la limpieza del contexto usuarios.
 *
 * <p>La asignación de rol vive solo en Keycloak (es autorización): {@code POST /usuarios} valida
 * cada valor recibido contra {@link #CONOCIDOS} en {@code RegistrarUsuarioCommand.crear} — un valor
 * fuera de la lista es un 400. No hay filas marcador de dominio ni eventos en HU256 para ninguno de
 * los ocho roles, salvo {@link #ESTUDIANTE}: desde HU-247 ese realm role también materializa una fila
 * en {@code estudiante} y publica {@code usuarios.estudiante.agregado}, consumido por {@code fichas}.
 */
public final class UsuariosRealmRoles {

    private UsuariosRealmRoles() {}

    public static final String ESTUDIANTE = "estudiante";
    public static final String ASESOR = "asesor";
    public static final String ASESOR_FICHA = "asesor-ficha";
    public static final String COORDINADOR = "coordinador";
    public static final String JURADO = "jurado";
    public static final String BIBLIOTECARIO = "bibliotecario";
    public static final String REPRESENTANTE_COMITE = "representante-comite";
    public static final String ADMINISTRADOR = "administrador";

    public static final Set<String> CONOCIDOS = Set.of(
            ESTUDIANTE, ASESOR, ASESOR_FICHA, COORDINADOR,
            JURADO, BIBLIOTECARIO, REPRESENTANTE_COMITE, ADMINISTRADOR);
}
