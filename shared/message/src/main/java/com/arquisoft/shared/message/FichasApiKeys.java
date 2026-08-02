package com.arquisoft.shared.message;

/**
 * Referencias a los textos de {@code messages/fichas-api.properties}, listas para usar en las
 * anotaciones de documentación OpenAPI ({@code @Tag}, {@code @Operation}, {@code @ApiResponse}).
 *
 * <p>Cada constante lleva la clave envuelta en {@code "${clave}"}: springdoc resuelve esa sintaxis
 * contra el {@code Environment} de Spring al construir la especificación. {@code MessageCatalogConfig}
 * registra el archivo como {@code PropertySource} para que esas claves estén disponibles.
 *
 * <p>Excepción: {@code TAG_NAME} y {@code TAG_DESCRIPTION} llevan el texto incrustado. springdoc
 * resuelve {@code ${...}} en {@code @Operation} y {@code @ApiResponse}, pero NO en {@code @Tag}
 * (verificado en springdoc 2.8.8: ningún punto del camino que construye los tags pasa por
 * PropertyResolverUtils), así que externalizarlos mostraría el literal ${clave} en Swagger UI.
 *
 * <p>Sustituye a la antigua clase {@code FichasApiDocs}, que llevaba los textos incrustados.
 */
public final class FichasApiKeys {

    private FichasApiKeys() {}

    private static final String ABRE = "${";
    private static final String CIERRA = "}";

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = ABRE + "fichas.infraestructura.comun.api.resp-401" + CIERRA;
        public static final String RESP_403 = ABRE + "fichas.infraestructura.comun.api.resp-403" + CIERRA;
    }

    public static final class FichaPerfil {

        private FichaPerfil() {}

        public static final String TAG_NAME = "Fichas de Perfil";
        public static final String TAG_DESCRIPTION = "Gestión de fichas de perfil de proyectos de grado";

        public static final String REGISTRAR_SUMMARY = ABRE + "fichas.infraestructura.fichaperfil.api.registrar-summary" + CIERRA;
        public static final String REGISTRAR_DESCRIPTION = ABRE + "fichas.infraestructura.fichaperfil.api.registrar-description" + CIERRA;
        public static final String REGISTRAR_RESP_201 = ABRE + "fichas.infraestructura.fichaperfil.api.registrar-resp-201" + CIERRA;
        public static final String REGISTRAR_RESP_400 = ABRE + "fichas.infraestructura.fichaperfil.api.registrar-resp-400" + CIERRA;

        public static final String MODIFICAR_SUMMARY = ABRE + "fichas.infraestructura.fichaperfil.api.modificar-summary" + CIERRA;
        public static final String MODIFICAR_DESCRIPTION = ABRE + "fichas.infraestructura.fichaperfil.api.modificar-description" + CIERRA;
        public static final String MODIFICAR_RESP_204 = ABRE + "fichas.infraestructura.fichaperfil.api.modificar-resp-204" + CIERRA;
        public static final String MODIFICAR_RESP_400 = ABRE + "fichas.infraestructura.fichaperfil.api.modificar-resp-400" + CIERRA;
        public static final String MODIFICAR_RESP_403 = ABRE + "fichas.infraestructura.fichaperfil.api.modificar-resp-403" + CIERRA;

        public static final String CAMBIAR_ASESOR_SUMMARY = ABRE + "fichas.infraestructura.fichaperfil.api.cambiar-asesor-summary" + CIERRA;
        public static final String CAMBIAR_ASESOR_DESCRIPTION =
                ABRE + "fichas.infraestructura.fichaperfil.api.cambiar-asesor-description" + CIERRA;
        public static final String CAMBIAR_ASESOR_RESP_204 = ABRE + "fichas.infraestructura.fichaperfil.api.cambiar-asesor-resp-204" + CIERRA;
        public static final String CAMBIAR_ASESOR_RESP_400 = ABRE + "fichas.infraestructura.fichaperfil.api.cambiar-asesor-resp-400" + CIERRA;
        public static final String CAMBIAR_ASESOR_RESP_422 = ABRE + "fichas.infraestructura.fichaperfil.api.cambiar-asesor-resp-422" + CIERRA;

        public static final String CONSULTAR_SUMMARY = ABRE + "fichas.infraestructura.fichaperfil.api.consultar-summary" + CIERRA;
        public static final String CONSULTAR_DESCRIPTION = ABRE + "fichas.infraestructura.fichaperfil.api.consultar-description" + CIERRA;
        public static final String CONSULTAR_RESP_200 = ABRE + "fichas.infraestructura.fichaperfil.api.consultar-resp-200" + CIERRA;
        public static final String CONSULTAR_RESP_400 = ABRE + "fichas.infraestructura.fichaperfil.api.consultar-resp-400" + CIERRA;
        public static final String CONSULTAR_RESP_403 = ABRE + "fichas.infraestructura.fichaperfil.api.consultar-resp-403" + CIERRA;
    }

    public static final class ItemFichaPerfil {

        private ItemFichaPerfil() {}

        public static final String TAG_NAME = "Ítems de Ficha de Perfil";
        public static final String TAG_DESCRIPTION = "Gestión de ítems de contenido de las fichas de perfil";

        public static final String AGREGAR_SUMMARY = ABRE + "fichas.infraestructura.itemfichaperfil.api.agregar-summary" + CIERRA;
        public static final String AGREGAR_DESCRIPTION = ABRE + "fichas.infraestructura.itemfichaperfil.api.agregar-description" + CIERRA;
        public static final String AGREGAR_RESP_201 = ABRE + "fichas.infraestructura.itemfichaperfil.api.agregar-resp-201" + CIERRA;
        public static final String AGREGAR_RESP_400 = ABRE + "fichas.infraestructura.itemfichaperfil.api.agregar-resp-400" + CIERRA;
        public static final String AGREGAR_RESP_403 = ABRE + "fichas.infraestructura.itemfichaperfil.api.agregar-resp-403" + CIERRA;
        public static final String AGREGAR_RESP_422 = ABRE + "fichas.infraestructura.itemfichaperfil.api.agregar-resp-422" + CIERRA;

        public static final String MODIFICAR_SUMMARY = ABRE + "fichas.infraestructura.itemfichaperfil.api.modificar-summary" + CIERRA;
        public static final String MODIFICAR_DESCRIPTION = ABRE + "fichas.infraestructura.itemfichaperfil.api.modificar-description" + CIERRA;
        public static final String MODIFICAR_RESP_204 = ABRE + "fichas.infraestructura.itemfichaperfil.api.modificar-resp-204" + CIERRA;
        public static final String MODIFICAR_RESP_400 = ABRE + "fichas.infraestructura.itemfichaperfil.api.modificar-resp-400" + CIERRA;
        public static final String MODIFICAR_RESP_403 = ABRE + "fichas.infraestructura.itemfichaperfil.api.modificar-resp-403" + CIERRA;
        public static final String MODIFICAR_RESP_422 = ABRE + "fichas.infraestructura.itemfichaperfil.api.modificar-resp-422" + CIERRA;

        public static final String REMOVER_SUMMARY = ABRE + "fichas.infraestructura.itemfichaperfil.api.remover-summary" + CIERRA;
        public static final String REMOVER_DESCRIPTION = ABRE + "fichas.infraestructura.itemfichaperfil.api.remover-description" + CIERRA;
        public static final String REMOVER_RESP_204 = ABRE + "fichas.infraestructura.itemfichaperfil.api.remover-resp-204" + CIERRA;
        public static final String REMOVER_RESP_400 = ABRE + "fichas.infraestructura.itemfichaperfil.api.remover-resp-400" + CIERRA;
        public static final String REMOVER_RESP_403 = ABRE + "fichas.infraestructura.itemfichaperfil.api.remover-resp-403" + CIERRA;
        public static final String REMOVER_RESP_422 = ABRE + "fichas.infraestructura.itemfichaperfil.api.remover-resp-422" + CIERRA;
    }

    public static final class EstudianteFichaPerfil {

        private EstudianteFichaPerfil() {}

        public static final String TAG_NAME = "Estudiantes de Ficha de Perfil";
        public static final String TAG_DESCRIPTION = "Gestión de la asignación de estudiantes a fichas de perfil";

        public static final String ASIGNAR_SUMMARY = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.asignar-summary" + CIERRA;
        public static final String ASIGNAR_DESCRIPTION = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.asignar-description" + CIERRA;
        public static final String ASIGNAR_RESP_204 = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.asignar-resp-204" + CIERRA;
        public static final String ASIGNAR_RESP_400 = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.asignar-resp-400" + CIERRA;
        public static final String ASIGNAR_RESP_403 = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.asignar-resp-403" + CIERRA;
        public static final String ASIGNAR_RESP_422 = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.asignar-resp-422" + CIERRA;

        public static final String REMOVER_SUMMARY = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.remover-summary" + CIERRA;
        public static final String REMOVER_RESP_204 = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.remover-resp-204" + CIERRA;
        public static final String REMOVER_RESP_400 = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.remover-resp-400" + CIERRA;
        public static final String REMOVER_RESP_403 = ABRE + "fichas.infraestructura.estudiantefichaperfil.api.remover-resp-403" + CIERRA;
    }

    public static final class EvaluacionFichaPerfil {

        private EvaluacionFichaPerfil() {}

        public static final String TAG_NAME = "Evaluaciones de Ficha de Perfil";
        public static final String TAG_DESCRIPTION = "Gestión de evaluaciones de fichas de perfil por el comité de currículum";

        public static final String REGISTRAR_SUMMARY = ABRE + "fichas.infraestructura.evaluacionfichaperfil.api.registrar-summary" + CIERRA;
        public static final String REGISTRAR_DESCRIPTION =
                ABRE + "fichas.infraestructura.evaluacionfichaperfil.api.registrar-description" + CIERRA;
        public static final String REGISTRAR_RESP_201 = ABRE + "fichas.infraestructura.evaluacionfichaperfil.api.registrar-resp-201" + CIERRA;
        public static final String REGISTRAR_RESP_400 = ABRE + "fichas.infraestructura.evaluacionfichaperfil.api.registrar-resp-400" + CIERRA;
    }

    public static final class EstadoEvaluacionFicha {

        private EstadoEvaluacionFicha() {}

        public static final String TAG_NAME = "Estados de Evaluación de Ficha";
        public static final String TAG_DESCRIPTION = "Gestión de trazabilidad de estados de evaluación de fichas de perfil";

        public static final String AGREGAR_SUMMARY = ABRE + "fichas.infraestructura.estadoevaluacionficha.api.agregar-summary" + CIERRA;
        public static final String AGREGAR_DESCRIPTION = ABRE + "fichas.infraestructura.estadoevaluacionficha.api.agregar-description" + CIERRA;
        public static final String AGREGAR_RESP_201 = ABRE + "fichas.infraestructura.estadoevaluacionficha.api.agregar-resp-201" + CIERRA;
        public static final String AGREGAR_RESP_400 = ABRE + "fichas.infraestructura.estadoevaluacionficha.api.agregar-resp-400" + CIERRA;
        public static final String AGREGAR_RESP_403 = ABRE + "fichas.infraestructura.estadoevaluacionficha.api.agregar-resp-403" + CIERRA;
        public static final String AGREGAR_RESP_422 = ABRE + "fichas.infraestructura.estadoevaluacionficha.api.agregar-resp-422" + CIERRA;
    }

    public static final class EstadoFicha {

        private EstadoFicha() {}

        public static final String TAG_NAME = "Estados de Ficha";
        public static final String TAG_DESCRIPTION = "Catálogo de estados del ciclo de vida de las fichas de perfil";

        public static final String CONSULTAR_SUMMARY = ABRE + "fichas.infraestructura.estadoficha.api.consultar-summary" + CIERRA;
        public static final String CONSULTAR_DESCRIPTION = ABRE + "fichas.infraestructura.estadoficha.api.consultar-description" + CIERRA;
        public static final String CONSULTAR_RESP_200 = ABRE + "fichas.infraestructura.estadoficha.api.consultar-resp-200" + CIERRA;
        public static final String CONSULTAR_RESP_401 = ABRE + "fichas.infraestructura.estadoficha.api.consultar-resp-401" + CIERRA;
        public static final String CONSULTAR_RESP_403 = ABRE + "fichas.infraestructura.estadoficha.api.consultar-resp-403" + CIERRA;
    }
}
