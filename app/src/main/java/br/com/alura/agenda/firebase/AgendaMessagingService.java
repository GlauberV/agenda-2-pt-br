package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Glauber on 24/08/2017.
 */

public class AgendaMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> mensagem = remoteMessage.getData();
        Log.i("Mensagem FCM", String.valueOf(mensagem));
    }
}
