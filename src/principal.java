import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import static Utils.base64.*;

public class principal {

    public static void main(String[] args)throws Exception {

        String login = "";
        String senha = "";
        //etapas para login

        /// abre site do HSL para obter cookie e viewstate
        Connection.Response res = Jsoup.connect("http://192.168.245.169/hospitaleHSL/principal.aspx")
                .userAgent("Mozilla/5.0")
                .header("Cache-Control", "no-cache")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .followRedirects(false)
                .header("Upgrade-Insecure-Requests", "1")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .timeout(0)
                .method(Connection.Method.GET)
                .execute();
        Document doc = res.parse();
        Map<String, String> sntCookies = res.cookies();
        sntCookies.remove("Eclipse"); // remove Eclipse cookie porque nao fez login ainda

        Elements viewState = doc.select("input[name=__VIEWSTATE]");
        Elements viewStateGenerator = doc.select("input[name=__VIEWSTATEGENERATOR]");
        Elements valorSessao = doc.select("input[name=valorSessao]");
        String viewStateStr = escapeStr(viewState.val());
        //viewStateStr = viewState.val().replaceAll("/","%2F");
        //viewStateStr = viewStateStr.replaceAll("=","%3D");
        //viewStateStr = viewStateStr.replaceAll("\\+","%2B");

        StringBuilder postParams = new StringBuilder();
        // melhor solução para trabalhar com estes params de POST do asp.NET,
        // porque usam caracteres especiais tanto em 'names' qto em values e
        // podem sofrer 'escape' pelo jsoup e perder sentido logico.
        // Dai preferi implentar POST em binario
        postParams.append("__LASTFOCUS=&smPadrao_HiddenField=&__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=");
        postParams.append(viewStateStr).append("&__VIEWSTATEGENERATOR=").append(viewStateGenerator.val());
        postParams.append("&hididUsuario=&hidModoNavegador=undefined&ultimoFoco=&valorSessao=").append(valorSessao.val());
        postParams.append("&horaSessao=").append(new Date().getTime());
        postParams.append("&hidStatusHoverTreeView=&abaativa=&idAba=1&wucLogin%24hidShift=&wucLogin%24hidExpressaoSenha=&wucLogin%24hidIP_CLIENTE=&wucLogin%24hidMAC_CLIENTE=&wucLogin%24hidMAQUINA_CLIENTE=&wucLogin%24txbUsuario=").
                append(login).append("&wucLogin%24txbSenha=").
                append(senha).append("&wucLogin%24btnOk=Entrar&hidMaquina=&isPrintApplet=False");

        byte[] postParamsBytes = postParams.toString().getBytes(StandardCharsets.UTF_8);

        // faz o POST com os parametros de login
        res = Jsoup.connect("http://192.168.245.169/hospitaleHSL/principal.aspx")
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .followRedirects(false)
                .header("Cache-Control", "no-cache")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Content-Length", String.valueOf(postParamsBytes.length))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .requestBodyBinary(postParamsBytes)
                .cookies(sntCookies)
                .timeout(0)
                .method(Connection.Method.POST).
                        execute();
        sntCookies.putAll(res.cookies());
        doc = res.parse();

        String respostaPostLogin = doc.html(); // armazena retorno do login, que sera importante em requests futuros

        // busca mensagens do usuario
        res = Jsoup.connect("http://192.168.245.169/hospitaleHSL/common/diaLogcOntaiNerscroll.aspx")
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .followRedirects(false)
                //.header("Connection","close")
                .header("Cache-Control", "no-cache")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .cookies(sntCookies)
                .timeout(0)
                .method(Connection.Method.GET)
                .execute();
        doc = res.parse();

        /// processa para viewState e viewStateGenerator
        /// simula o que o navegador faria
        viewState = doc.select("input[name=__VIEWSTATE]");
        viewStateGenerator = doc.select("input[name=__VIEWSTATEGENERATOR]");
        viewStateStr = viewState.val();
        viewStateStr = viewStateStr.replaceAll("/","%2F");
        viewStateStr = viewStateStr.replaceAll("=","%3D");
        viewStateStr = viewStateStr.replaceAll("\\+","%2B");

        postParams.setLength(0);
        postParams.append("__VIEWSTATE=").append(viewStateStr)
                .append("&__VIEWSTATEGENERATOR=").append(viewStateGenerator.val())
                .append("&hidPar1=overflow&hidPar2=&hidPar3=&hidPar4=&hidPar5=&hidPar6=");
        postParamsBytes = postParams.toString().getBytes(StandardCharsets.UTF_8);

        StringBuilder auxiliar = new StringBuilder(); // sera usado para a func pegaValor
        pegaValor(respostaPostLogin,"document.getElementById('tvMenut0').focus()",0,"','","',",auxiliar);
        String linkAviso = "http://192.168.245.169/hospitaleHSL/" + auxiliar.toString();

        // faz a requisicao dos avisos
        res = Jsoup.connect(linkAviso)
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .followRedirects(false)
                .header("Cache-Control", "no-cache")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Content-Length", String.valueOf(postParamsBytes.length))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .requestBodyBinary(postParamsBytes)
                .cookies(sntCookies)
                .timeout(0)
                .method(Connection.Method.POST).
                        execute();
        doc = res.parse();
        // valor retornado eh inutil para o programa, entao ignora

        postParams.setLength(0);
        /// Agora processa resposta do Post de login para iniciar processo de busca de pacientes
        /// requer busca de parametros hidden
        /// portencial de erro e necessidade de adaptar em versoes futuras
        pegaValor(respostaPostLogin,"<input type=\"hidden\" name=\"__VIEWSTATE\"",0,"value=\"","\"",auxiliar);
        viewStateStr = escapeStr(auxiliar);
        pegaValor(respostaPostLogin,"TSM_HiddenField",0,"TSM_CombinedScripts_=","\"",auxiliar);
        postParams.append("smPadrao=updMensagem%7CbtnVerificarMensagem&smPadrao_HiddenField=").append(auxiliar.toString()).
                append("&__EVENTTARGET=&__EVENTARGUMENT=&tvMenu_ExpandState=euununununnunnuunuununnnnnnnnnnnunununnuunuununuunnnuunnuunn&tvMenu_SelectedNode=&tvMenu_PopulateLog=&__VIEWSTATE=").
                append(viewStateStr).append("&__VIEWSTATEGENERATOR=");
        pegaValor(respostaPostLogin,"<input type=\"hidden\" name=\"__VIEWSTATEGENERATOR\"",0,"value=\"","\"",auxiliar);
        postParams.append(auxiliar).append("&hididUsuario=");
        pegaValor(respostaPostLogin,"<input name=\"hididUsuario\" type=\"hidden\"",0,"value=\"","\"",auxiliar);
        postParams.append(auxiliar).append("&hidModoNavegador=undefined&ultimoFoco=&acbAcessoRapido%24txbID=&acbAcessoRapido%24txbName=&acbAcessoRapido%24hidToolTip=&valorSessao=40&horaSessao=").
                append(new Date().getTime()).append("&hidStatusHoverTreeView=&abaativa=&idAba=1&hidMaquina=&isPrintApplet=False&hiddenInputToUpdateATBuffer_CommonToolkitScripts=1&__ASYNCPOST=true&btnVerificarMensagem=");
        postParamsBytes = postParams.toString().getBytes(StandardCharsets.UTF_8);

        /// faz o post com os parametros hidden, parece essencial agora efetivação do login/sessão
        res = Jsoup.connect("http://192.168.245.169/hospitaleHSL/principal.aspx")
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .followRedirects(false)
                .header("Cache-Control", "no-cache")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Content-Length", String.valueOf(postParamsBytes.length))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .requestBodyBinary(postParamsBytes)
                .cookies(sntCookies)
                .timeout(0)
                .method(Connection.Method.POST).
                        execute();
        doc = res.parse();
        /// resposta tambem nao parece ser importante

        ////////////////////////////
        ////// ate aqui foi a etapa de login e verificacao de mensagens
        ///////////////////////

        // seguem requests que o navegador realiza... talvez alguns possam ser suprimidos
        res = Jsoup.connect("http://192.168.245.169/hospitaleHSL/Clinico/ProntuarioEletronico/p_hosp_1401_PEP.aspx")
                .userAgent("Mozilla/5.0")
                //.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.128 Safari/537.36")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .followRedirects(false)
                //.header("Connection","close")
                //.header("Cache-Control", "no-cache")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .cookies(sntCookies)
                .timeout(0)
                .method(Connection.Method.GET)
                .execute();
        doc = res.parse();
        //// ignora resultado tambem, sem uso para o programa

        // pega janela de avisos, importante para viewState
        res = Jsoup.connect("http://192.168.245.169/hospitaleHSL/common/diaLogconTainerScroll.aspx")
                .userAgent("Mozilla/5.0")
                //.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.128 Safari/537.36")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .followRedirects(false)
                //.header("Connection","close")
                //.header("Cache-Control", "no-cache")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .cookies(sntCookies)
                .timeout(0)
                .method(Connection.Method.GET)
                .execute();
        doc = res.parse();

        viewState = doc.select("input[name=__VIEWSTATE]");
        viewStateGenerator = doc.select("input[name=__VIEWSTATEGENERATOR]");
        viewStateStr = escapeStr(viewState.val());

        postParams.setLength(0);
        postParams.append("__VIEWSTATE=").append(viewStateStr)
                .append("&__VIEWSTATEGENERATOR=").append(viewStateGenerator.val())
                .append("&hidPar1=overflow&hidPar2=&hidPar3=&hidPar4=&hidPar5=&hidPar6=");
        postParamsBytes = postParams.toString().getBytes(StandardCharsets.UTF_8);

        /// janela de busca de pacientes, acessada por post dos dados de diaLogconTainerScroll
        res = Jsoup.connect("http://192.168.245.169/hospitaleHSL/Paciente/p_hosp_5001_lista_pacientes_prontuario.aspx?retornar=ID_NUMERO_PRONTUARIO")
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .followRedirects(false)
                .header("Upgrade-Insecure-Requests", "1")
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Content-Length", String.valueOf(postParamsBytes.length))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .requestBodyBinary(postParamsBytes)
                .cookies(sntCookies)
                .timeout(0)
                .method(Connection.Method.POST).
                        execute();
        doc = res.parse();

        viewState = doc.select("input[name=__VIEWSTATE]");
        viewStateGenerator = doc.select("input[name=__VIEWSTATEGENERATOR]");
        viewStateStr = escapeStr(viewState.val());

        /// valores e presuncoes de post baseados no que o site faz, nao parecem ser dinamicos, com exceçao
        // das que eu busco com pegaValor(), se for dinamico tem potencia de erro no futuro
        pegaValor(doc.html(),"TSM_CombinedScripts",0,"=","\"",auxiliar);
        String scripts = auxiliar.toString().replaceAll("\\+","%20"); // convençao

        postParams.setLength(0);
        postParams.append("ScriptManager1=ScriptManager1%7CbtnPesquisar&__LASTFOCUS=&ScriptManager1_HiddenField=").append(scripts);
        postParams.append("&__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=").append(viewStateStr);
        postParams.append("&__VIEWSTATEGENERATOR=").append(viewStateGenerator.val());
        postParams.append("&__VIEWSTATEENCRYPTED=&hidSomenteLeituraSRES=0&txbID_ATENDIMENTO=&txbDataInicio=&txbDataFim=&ddlUnidadeOrganizacional=")
                .append("&txbProntuario=&txtPaciente=&ddlUnidadeLeito=78&txbCRMMedico=&ddlUFMedico=MG&txbNomeMedico=&hidID_MEDICO_SOLICITANTE=")
                .append("&ddlEspecialidade=&ddlID_STATUS_ATENDIMENTO=2894&chkPacientesUltimoAno=on&frmFiltro=&frmValor=&__ASYNCPOST=true&btnPesquisar=Pesquisar");

        postParamsBytes = postParams.toString().getBytes(StandardCharsets.UTF_8);

        // finalmente faz a busca de pacientes por leito usando todos os params acima em binario
        res = Jsoup.connect("http://192.168.245.169/hospitaleHSL/Paciente/p_hosp_5001_lista_pacientes_prontuario.aspx?retornar=ID_NUMERO_PRONTUARIO")
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .followRedirects(false)
                .header("Upgrade-Insecure-Requests", "1")
                .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                .header("Content-Length", String.valueOf(postParamsBytes.length))
                //.header("X-Requested-With", "XMLHttpRequest")
                //.header("X-MicrosoftAjax", "Delta=true")
                .header("Cache-Control", "no-cache")
                .header("Accept", "*/*")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .requestBodyBinary(postParamsBytes)
                .cookies(sntCookies)
                .timeout(0)
                .method(Connection.Method.POST).
                        execute();

        doc = res.parse(); /// resposta do POST da busca de pacientes por leito de CTI

        ////////////////// iniciar parse de lista de pacientes
        Element grid = doc.getElementById("pnGrid"); // pega tabela e ignora scripts
        Elements children = grid.getElementsByClass("tdnumerada"); // className unica de cada linha de paciente
        Elements pacientes = new Elements();
        for (Element elementoFilho : children){ // busca elemento pai de cada linha,
                // pois ha 2 tipos diferentes de className de parentNode e o parentNode contem as informacoes maiores
            Element elementoPai = elementoFilho.parent();
            pacientes.add(elementoPai);
        }
        JSONArray listaPacientes = new JSONArray();  // prepara JSON array com pacientes
        for (Element paciente : pacientes){
            JSONObject pac = new JSONObject();
            Elements pacienteChildren = paciente.children(); /// cada elemento tem filhos com as informacoes
            String nome = pacienteChildren.get(1).html();
            pac.put("nome",nome);
            String titulo = pacienteChildren.get(1).attributes().asList().get(0).getValue()+ "\n";
              // prontuario nao eh valor de nenhuma tag, mas eh encontrado no title desta tag
              // uso "\n" para marcacao de fim, me parece ser um parametro um pouco fragil...
              // prontuario pode ser obtido também no pacienteChildren.get(0).html() -->
            pegaValor(titulo,"Prontu",0,":","\n",auxiliar);
            pac.put("prontuario",auxiliar.toString().trim());
            pegaValor(titulo,"Nascimento",0,":","\n",auxiliar);
            pac.put("dataNasc",auxiliar.toString().trim());
            pegaValor(titulo,"Idade",0,":","\n",auxiliar);
            pac.put("idade",auxiliar.toString().trim());
            String atendimento = pacienteChildren.get(2).html();
            pac.put("atendimento",atendimento);
            pac.put("dtAtendimento",pacienteChildren.get(3).html());
            pac.put("leito",pacienteChildren.get(5).html());
            listaPacientes.put(pac);  // acrescenta object json do paciente na lista
        }

        ////// seguir com a parte de busca exames e montagem das tabelas ....

        return;
    }

    public static int pegaValor(String source, String marcadorExame, int offset, String marcadorInicio, String marcadorFim, StringBuilder resp)
    {
        resp.setLength(0);
        int x = source.indexOf(marcadorExame,offset);
        if (x < 0) return x;
        int y = source.indexOf(marcadorInicio,x) + marcadorInicio.length();
        x = source.indexOf(marcadorFim,y);

        try {
            resp.append(source.substring(y, x));
        }
        catch (Exception e) {resp.append ("?");}
        return x;
    }

    public static String escapeStr (String str){
        String aux = str.replaceAll("/","%2F");
        aux = aux.replaceAll("=","%3D");
        aux = aux.replaceAll("\\+","%2B");
        return aux;
    }

    public static String escapeStr (StringBuilder str){
        String aux = str.toString();
        aux = aux.replaceAll("/","%2F");
        aux = aux.replaceAll("=","%3D");
        aux = aux.replaceAll("\\+","%2B");
        return aux;
    }

    public static String unEscapeStr (String str){
        String aux = str.replaceAll("%2F","/");
        aux = aux.replaceAll("%3D","=");
        aux = aux.replaceAll("%2B","+");
        return aux;
    }

    public static String unEscapeStr (StringBuilder str){
        String aux = str.toString();
        aux = aux.replaceAll("%2F","/");
        aux = aux.replaceAll("%3D","=");
        aux = aux.replaceAll("%2B","+");
        return aux;
    }
}
