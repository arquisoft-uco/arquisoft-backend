package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas;

/**
 * Lectura tolerante del evento {@code fichas.ficha_perfil.asesor_cambiado}.
 *
 * <p>Es un record propio y no la clase del evento de {@code fichas} a proposito: asi
 * {@code notificaciones} no depende del contexto productor, y un campo nuevo en el origen no
 * rompe este consumidor —el {@code ObjectMapper} de RabbitMQ ignora las propiedades desconocidas.
 *
 * @param idEvento        identificador del evento — clave de idempotencia
 * @param fichaPerfilId  ficha afectada
 * @param tituloProyecto titulo de la ficha, para el texto del correo
 * @param asesorNombre   nombre del nuevo asesor
 * @param asesorEmail    correo del nuevo asesor
 */
public record AsesorFichaCambiadoPayload(
        String idEvento,
        String fichaPerfilId,
        String tituloProyecto,
        String asesorNombre,
        String asesorEmail) {
}
