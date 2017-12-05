package br.com.newidea.desafiotattooapp.utils;

/**
 * Created by fabio on 13/10/17.
 */

public class NullDeserializerUtils {

    public static String strNullToEmpty(String str) {
        return str == null ? "" : str;
    }
}
