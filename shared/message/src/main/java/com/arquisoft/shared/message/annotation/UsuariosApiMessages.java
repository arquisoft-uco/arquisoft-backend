package com.arquisoft.shared.message.annotation;

/**
 * Textos de documentación OpenAPI del contexto {@code usuarios}.
 *
 * <p>Mismo criterio que {@link FichasApiMessages}: el texto va incrustado y no al catálogo de Redis,
 * porque la especificación OpenAPI se construye una vez al arrancar y no vuelve a consultarse. Ver
 * allí el razonamiento completo.
 */
public final class UsuariosApiMessages {

    private UsuariosApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar esta acción";
    }

    public static final class Usuario {

        private Usuario() {}

        public static final String TAG_NAME = "Usuarios";
        public static final String TAG_DESCRIPTION = "Gestión de usuarios del sistema";

        public static final String REGISTRAR_SUMMARY = "Registrar información de un nuevo usuario";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra un nuevo usuario en el proveedor de identidad (Keycloak) y persiste la "
                        + "información local con el identificador que Keycloak le asigna. Asigna los realm "
                        + "roles indicados y dispara el correo de establecimiento de contraseña. "
                        + "Exclusivo del rol administrador.";
        public static final String REGISTRAR_RESP_201 = "Usuario registrado — retorna el UUID asignado";
        public static final String REGISTRAR_RESP_400 = "Datos de entrada inválidos o rol no válido";
        public static final String REGISTRAR_RESP_422 = "Identificador, email o contacto ya registrados, o datos de dominio inválidos";
        public static final String REGISTRAR_RESP_503 = "No fue posible completar el registro; el servicio no está disponible temporalmente";

        public static final String MODIFICAR_SUMMARY = "Modificar información de un usuario existente";
        public static final String MODIFICAR_DESCRIPTION =
                "Modifica los datos personales enviados de un usuario, cualquiera sea su estado, y sincroniza "
                        + "el email y el nombre en el proveedor de identidad (Keycloak). Los roles enviados se "
                        + "agregan o, si fueron eliminados lógicamente, se reactivan; un rol ausente de la lista "
                        + "no se revoca. Los nombres y los apellidos se envían juntos o ninguno de los dos; el "
                        + "nombre completo se deriva de ambos. Solo cambian los campos presentes en el body; el estado del usuario no "
                        + "cambia. Un usuario eliminado no puede modificarse; primero debe activarse mediante el "
                        + "cambio de estado. Exclusivo del rol administrador.";
        public static final String MODIFICAR_RESP_204 = "Usuario modificado";
        public static final String MODIFICAR_RESP_400 =
                "Identificador de usuario inválido, rol no válido o body sin ningún dato ni rol";
        public static final String MODIFICAR_RESP_422 =
                "Usuario inexistente o eliminado, datos ya usados por otro usuario, o rol ya vigente";
        public static final String MODIFICAR_RESP_503 =
                "No fue posible sincronizar con el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String ELIMINAR_SUMMARY = "Eliminar definitivamente la información de un usuario";
        public static final String ELIMINAR_DESCRIPTION =
                "Elimina lógicamente un usuario sin roles vigentes: marca la fecha de eliminación y, si estaba "
                        + "activo, lo pasa a inactivo, lo que deshabilita su identidad en el proveedor de identidad "
                        + "(Keycloak). El usuario se recupera activándolo mediante el cambio de estado. "
                        + "Exclusivo del rol administrador.";
        public static final String ELIMINAR_RESP_204 = "Usuario eliminado";
        public static final String ELIMINAR_RESP_400 = "Identificador de usuario inválido";
        public static final String ELIMINAR_RESP_422 = "Usuario inexistente, ya eliminado o con roles vigentes";
        public static final String ELIMINAR_RESP_503 =
                "No fue posible deshabilitar la identidad en el proveedor; el servicio no está disponible temporalmente";

        public static final String CAMBIAR_ESTADO_SUMMARY = "Cambiar la información de estado de un usuario existente";
        public static final String CAMBIAR_ESTADO_DESCRIPTION =
                "Activa o inactiva un usuario. Inactivar deshabilita su identidad en el proveedor de identidad "
                        + "(Keycloak); activar la habilita y, si el usuario estaba eliminado, lo restaura. "
                        + "Notifica el cambio al usuario por correo. Exclusivo del rol administrador.";
        public static final String CAMBIAR_ESTADO_RESP_204 = "Estado del usuario cambiado";
        public static final String CAMBIAR_ESTADO_RESP_400 = "Identificador de usuario inválido o estado no enviado";
        public static final String CAMBIAR_ESTADO_RESP_422 =
                "Usuario inexistente, estado fuera del catálogo o igual al actual";
        public static final String CAMBIAR_ESTADO_RESP_503 =
                "No fue posible actualizar la identidad en el proveedor; el servicio no está disponible temporalmente";

        // TODO HU252: agregar jurado a la lista de roles de CONSULTAR_ADMINISTRADOR_DESCRIPTION.
        public static final String CONSULTAR_ADMINISTRADOR_SUMMARY =
                "Consultar información de los usuarios (administrador)";
        public static final String CONSULTAR_ADMINISTRADOR_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de todos los usuarios, incluidos los "
                        + "eliminados (vigente = false). Indica con un booleano por rol si el usuario tiene hoy el rol vigente "
                        + "de estudiante, asesor, asesor de ficha, coordinador, representante del comité, administrador o bibliotecario; "
                        + "esos booleanos "
                        + "se combinan con OR/AND en el filtro. Exclusivo del rol administrador.";
        public static final String CONSULTAR_RESP_200 = "Página de usuarios";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";

        public static final String CONSULTAR_IDENTIDAD_SUMMARY = "Consultar la identidad de un usuario";
        public static final String CONSULTAR_IDENTIDAD_DESCRIPTION =
                "Devuelve los nombres y apellidos vigentes que el proveedor de identidad tiene del usuario, "
                        + "para precargar el formulario de modificación. Falla si el usuario no existe o está "
                        + "eliminado. Exclusivo del rol administrador.";
        public static final String CONSULTAR_IDENTIDAD_RESP_200 = "Nombres y apellidos del usuario";
        public static final String CONSULTAR_IDENTIDAD_RESP_400 = "Identificador de usuario inválido";
        public static final String CONSULTAR_IDENTIDAD_RESP_422 = "Usuario inexistente o eliminado";
        public static final String CONSULTAR_IDENTIDAD_RESP_503 =
                "No fue posible leer la identidad en el proveedor; el servicio no está disponible temporalmente";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String REMOVER_SUMMARY = "Remover información de un estudiante";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol estudiante de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role estudiante en Keycloak y notifica a los contextos "
                        + "que replican al estudiante. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Estudiante removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol estudiante vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String CONSULTAR_VIGENTES_SUMMARY = "Consultar información de los estudiantes vigentes";
        public static final String CONSULTAR_VIGENTES_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de los estudiantes con el rol vigente. Expone y "
                        + "permite filtrar por el estado del usuario, pero no la vigencia. Disponible para "
                        + "asesores, asesores de ficha, coordinadores y representantes del comité.";
        public static final String CONSULTAR_RESP_200 = "Página de estudiantes";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String REMOVER_SUMMARY = "Remover información de un asesor";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol asesor de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role asesor en Keycloak y notifica a los contextos "
                        + "que replican al asesor. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Asesor removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol asesor vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String CONSULTAR_VIGENTES_SUMMARY = "Consultar información de los asesores vigentes";
        public static final String CONSULTAR_VIGENTES_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de los asesores con el rol vigente. Expone y "
                        + "permite filtrar por el estado del usuario, pero no la vigencia. Disponible para "
                        + "asesores, coordinadores y asesores de ficha.";
        public static final String CONSULTAR_RESP_200 = "Página de asesores";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }

    public static final class AsesorFicha {

        private AsesorFicha() {}

        public static final String REMOVER_SUMMARY = "Remover información de un asesor de ficha";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol asesor de ficha de un usuario: marca la fecha de eliminación sin "
                        + "borrar la fila, revoca el realm role asesor-ficha en Keycloak y notifica a los "
                        + "contextos que replican al asesor de ficha. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Asesor de ficha removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol asesor de ficha vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String CONSULTAR_VIGENTES_SUMMARY =
                "Consultar información de los asesores de ficha vigentes";
        public static final String CONSULTAR_VIGENTES_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de los asesores de ficha con el rol vigente. Expone y "
                        + "permite filtrar por el estado del usuario, pero no la vigencia. Disponible para "
                        + "asesores de ficha y representantes del comité de currículo.";
        public static final String CONSULTAR_RESP_200 = "Página de asesores de ficha";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }

    public static final class Coordinador {

        private Coordinador() {}

        public static final String REMOVER_SUMMARY = "Remover información de un coordinador";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol coordinador de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role coordinador en Keycloak y notifica a los contextos "
                        + "que replican al coordinador. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Coordinador removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol coordinador vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String CONSULTAR_VIGENTES_SUMMARY = "Consultar información de los coordinadores vigentes";
        public static final String CONSULTAR_VIGENTES_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de los coordinadores con el rol vigente. Expone y "
                        + "permite filtrar por el estado del usuario, pero no la vigencia. Disponible para "
                        + "asesores, estudiantes y coordinadores.";
        public static final String CONSULTAR_RESP_200 = "Página de coordinadores";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }

    public static final class Bibliotecario {

        private Bibliotecario() {}

        public static final String REMOVER_SUMMARY = "Remover información de un bibliotecario";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol bibliotecario de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role bibliotecario en Keycloak y notifica a los contextos "
                        + "que replican al bibliotecario. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Bibliotecario removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol bibliotecario vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";
    }

    public static final class RepresentanteComite {

        private RepresentanteComite() {}

        public static final String REMOVER_SUMMARY = "Remover información de un representante del comité";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol representante del comité de un usuario: marca la fecha de eliminación "
                        + "sin borrar la fila, revoca el realm role representante-comite en Keycloak y notifica a "
                        + "los contextos que replican al representante. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Representante del comité removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol representante del comité vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String CONSULTAR_VIGENTES_SUMMARY =
                "Consultar información de los representantes del comité vigentes";
        public static final String CONSULTAR_VIGENTES_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de los representantes del comité con el rol vigente. Expone "
                        + "y permite filtrar por el estado del usuario, pero no la vigencia. Disponible para "
                        + "coordinadores, asesores de ficha y representantes del comité de currículo.";
        public static final String CONSULTAR_RESP_200 = "Página de representantes del comité";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }

    public static final class EstadoUsuario {

        private EstadoUsuario() {}

        public static final String CONSULTAR_SUMMARY =
                "Consultar información de todos los estados disponibles para los usuarios";
        public static final String CONSULTAR_DESCRIPTION =
                "Retorna el catálogo completo de estados que puede tener un usuario, con su identificador, "
                        + "nombre y descripción. Exclusivo del rol administrador.";
        public static final String CONSULTAR_RESP_200 = "Catálogo de estados de usuario";
    }

    public static final class Administrador {

        private Administrador() {}

        public static final String REMOVER_SUMMARY = "Remover información de un administrador";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol administrador de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role administrador en Keycloak y notifica a los contextos "
                        + "que replican al administrador. El usuario y sus demás roles no cambian. Un "
                        + "administrador no puede removerse a sí mismo ni remover al único administrador "
                        + "vigente. Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Administrador removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 =
                "El usuario no tiene un rol administrador vigente, es el propio solicitante o es el único "
                        + "administrador vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";
    }
}
