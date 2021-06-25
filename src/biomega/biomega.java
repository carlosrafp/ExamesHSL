package biomega;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Utils.strings.ISOtoUTF8;

public class biomega {

    private static List<String> cookies;
    private static String unidade = "106"; // 106 - SAO LUCAS // 105 - santa Casa  // 107 - cem

    public biomega(){}

    public void FazerLoginBiomega() throws Exception {

        Map<String,String> allFields2 = new HashMap<String,String>();
        allFields2.put("ztmAction", "S");
        allFields2.put("ztmLogin", "MEDICOS.HSC");
        allFields2.put("ztmSenha", "BIOMEGA");
        allFields2.put("ztmCliente", "");
        allFields2.put("ztmOptLogin", "consultas");
        allFields2.put("ztmClienteStatus", "undefined");
        allFields2.put("ztmSitesExternos", "");

        Connection.Response res = Jsoup.connect("https://biomega.lisnet.com.br/laudos/#")
                .userAgent("Mozilla/5.0")
                .data(allFields2)
                .method(Connection.Method.POST)
                .timeout(0)
                .execute();
        Document doc = res.parse();
        Map<String,String> biomegaCookies = res.cookies();
        this.cookies = new ArrayList<>();
        for (Map.Entry<String, String> entry : biomegaCookies.entrySet()) {
            this.cookies.add(entry.getKey() + "=" + entry.getValue());
        }
    }

    public String PegaManifesto (String paciente, int tries) {

        Map<String, String> allFields = new HashMap<String, String>();
        allFields.put("_search", "false");
        allFields.put("sidx", "");
        allFields.put("sord", "asc");
        allFields.put("ztmUnidade", unidade);
        allFields.put("ztmRequisicao", "");
        allFields.put("ztmSetor", "");
        allFields.put("ztmPosto", "");
        allFields.put("ztmProntuario", paciente);
        allFields.put("ztmOptsMultiplas", "pac_in_codsus");
        allFields.put("ztmCodMultiplos", "");
        allFields.put("ztmPacNomeA", "");
        allFields.put("ztmPacNomeB", "");
        allFields.put("ztmPacNomeC", "");
        allFields.put("ztmProcedimento", "");
        allFields.put("ztmCkImpresso", "undefined");
        allFields.put("ztmCkLiberado", "undefined");

        try {
            String url = "https://biomega.lisnet.com.br/laudos/consultas/z_Selects.php?down=R&page=1&rows=5";
            String cookie = cookies.get(0);
            Connection.Response res = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .data(allFields)
                    .method(Connection.Method.POST)
                    .cookie(cookie.split("=")[0], cookie.split("=")[1])
                    .timeout(0)
                    .execute();
            Document doc = res.parse();
            return doc.text();
        } catch (Exception e) {
            if (tries < 10) return PegaManifesto(paciente, tries + 1);
        }
        return "";
    }

    public String PegaDetalhes (String solicitacao, int tries) {

        Map<String, String> allFields = new HashMap<String, String>();
        allFields.put("_search", "false");
        allFields.put("nd", "" + new Date().getTime());
        allFields.put("rows", "10000");
        allFields.put("page", "1");
        allFields.put("sidx", "");
        allFields.put("sord", "asc");
        allFields.put("ztmRequisicaoId", solicitacao);
        allFields.put("ztmUnidadeCod", unidade);
        allFields.put("ztmSetorCod", "");
        allFields.put("ztmGrava", "S");
        allFields.put("ztmOrder", "");

        try {
            String url = "https://biomega.lisnet.com.br/laudos/consultas/z_Selects.php?down=D&page=1&rows=10000";
            String cookie = cookies.get(0);
            Connection.Response res = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .data(allFields)
                    .method(Connection.Method.POST)
                    .cookie(cookie.split("=")[0], cookie.split("=")[1])
                    .timeout(0)
                    .execute();
            Document doc = res.parse();
            return doc.text();
        } catch (Exception e) {
            if (tries < 10) return PegaDetalhes(solicitacao, tries + 1);
        }
        return "";
    }

    public static String procuraExame(String oque, JSONObject onde, int comNome){
        int pos;
        int y = onde.getJSONArray("exames").length();  // qtos exames liberados
        for (pos=0; pos < y;pos++){  // procura exames liberados
            int entrada = onde.getJSONArray("exames").getInt(pos); // pega primeiro/proximo exame liberado
            if (onde.getJSONArray("rows").getJSONObject(entrada).getJSONArray("cell").getString(0).indexOf(oque) >= 0) {
                // achou exame
                int ehArray = -1;
                int fim = onde.getJSONArray("rows").length();  // numero de linhas com nome de exame e resultado
                for(y=entrada+1;y < fim;y++){  // descobre se eh array
                    if ((onde.getJSONArray("rows").getJSONObject(y).getJSONArray("cell").length()) == 8) { // 8 celulas eh resultado, 9 eh nome de exame
                        ehArray++;
                    }
                    else break;
                }
                String valor = "";
                if (comNome > 0){  // pega nome do exame
                    valor = onde.getJSONArray("rows").getJSONObject(entrada).getJSONArray("cell").getString(0);
                    valor = valor.split(" - ")[0];  // pega codigo do exame, que antecede nome (ex: CAT - CALCIO TOTAL)
                    valor += " ";
                }
                if (ehArray > 0){  // resultado tem mais de uma linda de resultado, libera como: "nome = [parte1: result1 , part2: result2 ,...]"
                    fim = y;
                    // valor += comNome > 0 ? "= [" : "["; // exame eh uma array, avaliar se fica o igual
                    valor += "["; // exame eh uma array, avaliar se fica o igual
                    for (y=entrada+1;y < fim;y++){   // pega cada componente do exame
                        String subnome = onde.getJSONArray("rows").getJSONObject(y).getJSONArray("cell").getString(0).trim();
                        if (subnome.endsWith(":")) valor += subnome + " "; // se nome do resultado terminar com ':' nao acrescentar outro
                        else valor += subnome + ": ";
                        //String valorCru = onde.getJSONArray("rows").getJSONObject(y).getJSONArray("cell").getString(2);
                        valor += onde.getJSONArray("rows").getJSONObject(y).getJSONArray("cell").getString(2).replaceAll("\\<[a-zA-Z0-9/]+\\>","");
                        // remove <b> </b> e outros dados de html sem interesse
                        valor += " , ";
                    }
                    valor = valor.substring(0,valor.lastIndexOf(" , ")) + "]";  // retira a ultima virgula e fecha o colchete

                }
                else{  // resultado eh simples
                    valor += onde.getJSONArray("rows").getJSONObject(entrada+1).getJSONArray("cell").getString(2).trim().replaceAll("\\<[a-zA-Z0-9/]+\\>","");
                    // remove <b> </b> e outros dados de html sem interesse
                }
                onde.getJSONArray("exames").remove(pos);  // remover exame ja encontrado
                valor = valor.replaceAll("(&GT;)|(&GT)", ">"); // corrige >
                valor = valor.replaceAll("(&LT;)|(&LT)", "<"); // corrige <
                return valor;  // ja achou exame, retornar valor
            }
        }
        /// se chegou aqui o exame pedido nao esta na lista dos exames com laudo
        y = onde.getJSONArray("examesPendentes").length();  // numero de exames sem laudo definitivo
        for (pos=0; pos < y;pos++){  // procura exame 'oque' nos exames nao liberados
            int entrada = onde.getJSONArray("examesPendentes").getInt(pos); // pega primeiro/proximo exame da lista
            if (onde.getJSONArray("rows").getJSONObject(entrada).getJSONArray("cell").getString(0).indexOf(oque) >= 0) {
                // achou o exame na lista de pendentes
                onde.getJSONArray("examesPendentes").remove(pos);  // remover exame da lista
                if (onde.getJSONArray("rows").getJSONObject(entrada).getJSONArray("cell").getString(6).equals("013")){
                    return comNome == 0 ? "CANCELADO" : ""; // exame cancelado
                }
                String valor = "";
                if (comNome > 0){  // quer exame com nome do exame antes do resultado
                    valor = onde.getJSONArray("rows").getJSONObject(entrada).getJSONArray("cell").getString(0); // pega nome do exame
                    valor = valor.split(" - ")[0];  // pega codigo do exame, que antecede nome (ex: CAT - CALCIO TOTAL)
                    valor += " ";
                }
                // valor += comNome > 0 ? "= [" : "["; // exame eh uma array, avaliar se fica o igual
                valor += "["; // exame eh uma array, avaliar se fica o igual
                valor += onde.getJSONArray("rows").getJSONObject(entrada).getJSONArray("cell").getString(2).trim();
                valor += "]";
                valor = valor.replaceAll("(&GT;)|(&GT)", ">"); // corrige >
                valor = valor.replaceAll("(&LT;)|(&LT)", "<"); // corrige <
                return valor;
            }
        }

        return "";
    }

    public static int temExame(String oque, JSONObject onde){  // procura se o exame existe e retorna a posicao do exame ou -1
        int pos;
        int y = onde.getJSONArray("exames").length();  // qtos exames liberados
        for (pos=0; pos < y;pos++){  // procura exames liberados
            int entrada = onde.getJSONArray("exames").getInt(pos); // pega primeiro/proximo exame liberado
            if (onde.getJSONArray("rows").getJSONObject(entrada).getJSONArray("cell").getString(0).indexOf(oque) >= 0) {
                return entrada;// achou exame
            }
        }
        /// se chegou aqui o exame pedido nao esta na lista dos exames com laudo
        y = onde.getJSONArray("examesPendentes").length();  // numero de exames sem laudo definitivo
        for (pos=0; pos < y;pos++){  // procura exame 'oque' nos exames nao liberados
            int entrada = onde.getJSONArray("examesPendentes").getInt(pos); // pega primeiro/proximo exame da lista
            if (onde.getJSONArray("rows").getJSONObject(entrada).getJSONArray("cell").getString(0).indexOf(oque) >= 0) {
                // achou o exame na lista de pendentes
                return entrada;
            }
        }
        return -1;
    }

    public static String PegaExame (String url) {

        StringBuffer response = new StringBuffer();
        String resposta;
        try{
            String USER_AGENT = "Mozilla/5.0";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            //responseCode = con.getResponseCode();
            //System.out.println("\nSending 'GET' request to URL : " + url);
            //System.out.println("Response Code : " + responseCode);
            try{
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream(),"iso-8859-1")); // laudo vem em iso
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                resposta = response.toString();
                resposta = ISOtoUTF8(resposta); //converter para UTF-8 para compatibilidade
                //resposta = ui8paraStr(response.toString().getBytes("UTF-8"));  // nao funciona
            }
            catch(Exception e){
                return "";
            }

        }
        catch (Exception e){
            return "";
        }
        //return response.toString();
        return resposta;
    }
}
