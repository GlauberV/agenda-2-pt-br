package br.com.alura.agenda.firebase;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.AlunoSync;
import br.com.alura.agenda.event.AtualizaListaAlunoEvent;

/**
 * Created by Glauber on 24/08/2017.
 */

public class AgendaMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> mensagem = remoteMessage.getData();
        Log.i("Mensagem FCM", String.valueOf(mensagem));

        converteParaAluno(mensagem);
    }

    private void converteParaAluno(Map<String, String> mensagem) {
        String chaveDeAcesso = "alunoSync";
        if (mensagem.containsKey(chaveDeAcesso)) {
            String json = mensagem.get(chaveDeAcesso);

            ObjectMapper mapper = new ObjectMapper();
            try {
                AlunoSync alunoSync = mapper.readValue(json, AlunoSync.class);
                AlunoDAO dao = new AlunoDAO(this);
                dao.sincroniza(alunoSync.getAlunos());
                dao.close();

                EventBus eventBus = EventBus.getDefault();
                eventBus.post(new AtualizaListaAlunoEvent());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

//  "String json = mensagem.get(chaveDeAcesso);" Esse é o json do nosso(s) aluno(s) e precisamos
// converte-lo para um objeto do tipo Aluno e para isso usaremos o Jackson, nosso conversor.

// Mas antes precisaremos de um ObjectMapper -> "ObjectMapper mapper = new ObjectMapper();"

// "AlunoSync alunoSync = mapper.readValue(json, AlunoSync.class);" O metodo readValue() irá
// funcionar assim: "Converterei isso: _____ em isso _____." No caso o json com alunos para a nossa
// clase AlunoSync().

// "AlunoDAO dao = new AlunoDAO(this);" A FirebaseMessagingService possui uma herança interna da
// classe Context.

// Lembre-se que o método converteParaAlunos() é uma tentativa de atualizar a nossa lista de alunos
// automáticamente. Porém, o que fizemos com esse(s) aluno(s) foi apenas adiciona-los ao SQLite, e
// ainda precisamos fazer com que a lista seja atualiza automaticamente.

// Para facilitar a comunicação entre entidades do android studio, usaremos um biblioteca chamada
// EventBus. Criando um objeto com a partir do método estático "getDefault();". Em seguida usamos o
// método "post();" e passamos uma classe que não receberá implementação e servirá apenas como
// parâmetro avisando que o evento foi disparado.