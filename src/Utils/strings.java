package Utils;

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

}
