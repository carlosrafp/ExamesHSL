package Utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class strings {
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

    public static String pegaValorStr(String source, String marcadorExame, int offset, String marcadorInicio, String marcadorFim)
    {
        StringBuilder resp = new StringBuilder();
        int x = source.indexOf(marcadorExame,offset);
        if (x < 0) return "";
        int y = source.indexOf(marcadorInicio,x) + marcadorInicio.length();
        x = source.indexOf(marcadorFim,y);

        try {
            resp.append(source.substring(y, x));
        }
        catch (Exception e) {resp.append ("?");}
        return resp.toString();
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

    public static int buscaStringnaArray(String oque, String[] onde){
        int y = onde.length;
        for (int x =0; x < y; x++){
            if ((onde[x].indexOf(oque))>=0) return x;
        }
        return -1;
    }
    public static String ISOtoUTF8(String str){

        try{
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(str);
            String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
            return utf8EncodedString;
        }
        catch(Exception e){
            return "";
        }
    }


}
