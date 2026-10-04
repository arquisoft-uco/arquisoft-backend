package com.arquisoft.shared.message.constant;

public final class EventTopics {

    private EventTopics() {}

    public static final class Fichas {

        private Fichas() {}

        public static final String FICHA_PERFIL_ASESOR_CAMBIADO =
                "fichas.ficha_perfil.asesor_cambiado";

        public static final String FICHA_PERFIL_REGISTRADA =
                "fichas.ficha_perfil.registrada";

        public static final String ESTUDIANTES_FICHA_PERFIL_ASIGNADOS =
                "fichas.estudiante_ficha_perfil.asignados";

        public static final String REVISION_ITEM_AGREGADO =
                "fichas.revision_item.agregado";

        public static final String FICHA_PERFIL_APROBADA =
                "fichas.ficha_perfil.aprobada";

        public static final String FICHA_PERFIL_NO_APROBADA =
                "fichas.ficha_perfil.no_aprobada";

        public static final String ESTADO_FICHA_PERFIL_AGREGADO =
                "fichas.estado_ficha_perfil.agregado";
    }

    public static final class Proyectos {

        private Proyectos() {}

        public static final String PROYECTO_GRADO_REGISTRADO =
                "proyectos.proyecto_grado.registrado";

        public static final String ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS =
                "proyectos.estudiante_proyecto_grado.asignados";
    }

    public static final class Usuarios {

        private Usuarios() {}

        public static final String USUARIO_MODIFICADO =
                "usuarios.usuario.modificado";
        public static final String USUARIO_ESTADO_CAMBIADO =
                "usuarios.usuario.estado_cambiado";

        public static final String ESTUDIANTE_AGREGADO =
                "usuarios.estudiante.agregado";
        public static final String ESTUDIANTE_REMOVIDO =
                "usuarios.estudiante.removido";

        public static final String COORDINADOR_AGREGADO =
                "usuarios.coordinador.agregado";
        public static final String COORDINADOR_REMOVIDO =
                "usuarios.coordinador.removido";
        public static final String ASESOR_FICHA_AGREGADO =
                "usuarios.asesorficha.agregado";
        public static final String ASESOR_FICHA_REMOVIDO =
                "usuarios.asesorficha.removido";

        public static final String ASESOR_AGREGADO =
                "usuarios.asesor.agregado";
        public static final String ASESOR_REMOVIDO =
                "usuarios.asesor.removido";

        public static final String REPRESENTANTE_COMITE_AGREGADO =
                "usuarios.representantecomite.agregado";
        public static final String REPRESENTANTE_COMITE_REMOVIDO =
                "usuarios.representantecomite.removido";

        public static final String ADMINISTRADOR_AGREGADO =
                "usuarios.administrador.agregado";
        public static final String ADMINISTRADOR_REMOVIDO =
                "usuarios.administrador.removido";

        public static final String BIBLIOTECARIO_AGREGADO =
                "usuarios.bibliotecario.agregado";
        public static final String BIBLIOTECARIO_REMOVIDO =
                "usuarios.bibliotecario.removido";
    }

    public static final class Evaluaciones {

        private Evaluaciones() {}

        public static final String EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS =
                "evaluaciones.evaluacion_cualitativa_jurado.registradas";
    }

    public static final class Solicitudes {

        private Solicitudes() {}

        public static final String NOVEDAD_COORDINADOR_ENVIADA =
                "solicitudes.solicitud.novedad_coordinador_enviada";

        public static final String NOVEDAD_ASESOR_ENVIADA =
                "solicitudes.solicitud.novedad_asesor_enviada";

        public static final String CAMBIO_ASESOR_ENVIADA =
                "solicitudes.solicitud.cambio_asesor_enviada";

        public static final String AMPLIACION_PLAZO_ENVIADA =
                "solicitudes.solicitud.ampliacion_plazo_enviada";

        public static final String NOVEDAD_COORDINADOR_RESPONDIDA =
                "solicitudes.respuesta.novedad_coordinador_respondida";

        public static final String NOVEDAD_COORDINADOR_ESTADO_MODIFICADO =
                "solicitudes.respuesta.novedad_coordinador_estado_modificado";
    }

    public static final class MapasRuta {

        private MapasRuta() {}

        public static final String MAPA_RUTA_AGREGADO =
                "mapas_ruta.mapa_ruta.agregado";
    }
}
