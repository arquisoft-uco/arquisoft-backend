-- La idempotencia pasa de ser por evento a ser por evento y destinatario.
--
-- Hasta ahora un evento producia como mucho un correo, asi que event_id UNIQUE bastaba. Con
-- FichaPerfilRegistradaEvent un mismo evento notifica al asesor y a cada estudiante: con la
-- restriccion anterior el primer correo se enviaba y el resto se descartaba como duplicado, en
-- silencio, porque el consumidor registra el duplicado como un caso esperado y confirma el mensaje.
--
-- La pareja sigue cortando la reentrega de RabbitMQ, que es para lo que existia la restriccion, y
-- ademas deja el event_id del evento tal cual en la fila: se puede ver que correos produjo cada
-- evento, cosa que una clave derivada por destinatario habria ocultado.

ALTER TABLE notificacion DROP CONSTRAINT uq_notificacion_event_id;

ALTER TABLE notificacion
    ADD CONSTRAINT uq_notificacion_event_id_destinatario UNIQUE (event_id, destinatario);
