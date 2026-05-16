package com.arquisoft.seguridad.application.auth.port;

import java.util.List;

/**
 * Puerto de salida para obtener informacion del usuario autenticado actual.
 * La aplicacion define el contrato; la infraestructura implementa la obtencion
 * desde el contexto de seguridad de Spring.
 *
 * Nota: Se retornan tipos primitivos y String para mantener la aplicacion
 * libre de dependencias de framework (no se expone Authentication ni Jwt).
 */
public interface CurrentUserPort {

    /**
     * Obtiene el ID del usuario actual (subject del JWT).
     *
     * @return ID del usuario o null si no hay sesion activa
     */
    String getCurrentUserId();

    /**
     * Obtiene el email del usuario actual.
     *
     * @return email del usuario o null si no hay sesion activa
     */
    String getCurrentEmail();

    /**
     * Obtiene el nombre de usuario actual.
     *
     * @return nombre de usuario o null si no hay sesion activa
     */
    String getCurrentUsername();

    /**
     * Verifica si el usuario actual tiene un rol especifico.
     *
     * @param role el rol a verificar
     * @return true si el usuario tiene el rol
     */
    boolean hasRole(String role);

    /**
     * Obtiene los roles del usuario actual.
     *
     * @return lista de roles o lista vacia si no hay sesion activa
     */
    List<String> getCurrentUserRoles();
}
