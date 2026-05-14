package ika_deen.back_end.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmService {

    /**
     * Envoie une notification à un utilisateur spécifique via son token FCM.
     */
    public void sendNotification(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            log.warn("Tentative d'envoi de notification sans token.");
            return;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Notification envoyée avec succès : {}", response);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification FCM : {}", e.getMessage());
        }
    }

    /**
     * Envoie une notification à un topic (ex: "evenements").
     */
    public void sendToTopic(String topic, String title, String body) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(notification)
                    .build();

            FirebaseMessaging.getInstance().send(message);
            log.info("Notification envoyée au topic : {}", topic);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi au topic {} : {}", topic, e.getMessage());
        }
    }
}
