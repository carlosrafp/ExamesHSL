import Utils.strings;
import hospitale.hospitale;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Map;

import static Utils.arquivo.WriteFile;
import static Utils.base64.*;
import static Utils.strings.pegaValor;


public class principal {

    public static void main(String[] args)throws Exception {

        String login = "xxxx";
        String senha = "xxxx";

        hospitale hsc = new hospitale();

        try{
            if (hsc.fazerLogin(login,senha) < 0) {
                throw new Exception("fazerLogin() retornou -1");
            }
        }catch (Exception e){
            System.out.println("Erro ao logar no hospitale");
            System.out.println("Erro = " + e.toString());
            System.exit(1);
        }

       try{
           if (hsc.buscarListaPacientes() < 0) {
               throw new Exception("buscarListaPacientes() retornou -1");
           }
       }catch (Exception e){
           System.out.println("Erro ao buscar lista de pacientes");
           System.out.println("Erro = " + e.toString());
           System.exit(1);
       }

        String lista = hsc.getListaPacientesStr();
        ////// seguir com a parte de busca exames e montagem das tabelas ....
        WriteFile ("resposta.txt",lista);
        return;
    }



}
