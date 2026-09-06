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
    }

    public static final class Usuarios {

        private Usuarios() {}

        public static final String USUARIO_CREADO = "usuarios.usuario.creado";
    }

    public static final class Solicitudes {

        private Solicitudes() {}

        public static final String NOVEDAD_COORDINADOR_ENVIADA =
                "solicitudes.solicitud.novedad_coordinador_enviada";

        public static final String NOVEDAD_ASESOR_ENVIADA =
                "solicitudes.solicitud.novedad_asesor_enviada";

        public static final String CAMBIO_ASESOR_ENVIADA =
                "solicitudes.solicitud.cambio_asesor_enviada";
    }
}
