package io.github.eylexlive.leaderboards.util;

import io.github.eylexlive.leaderboards.uLeaderboards;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class License {

    private final uLeaderboards plugin;

    private final String licenseKey, validationServer, securityKey;

    private LogType logType = LogType.NORMAL;

    private boolean debug = false;

    public License(String licenseKey){
        this.licenseKey = licenseKey;
        this.plugin = uLeaderboards.getInstance();
        this.securityKey = "YecoF0I6M05thxLeokoHuW8iUhTdIUInjkfF";
        this.validationServer = this.v();
    }

    public boolean a(){
        final Type vt = this.l();
        log(0,"----------------------------------------");
        log(0, "      uLeaderboards      ");
        log (0, " ");
        if(vt == Type.VALID){
            log(1, "    License is valid.    ");
            log(1, " Thank you for purchasing! ");
            log (0, " ");
            log(0, "      uLeaderboards      ");
            log(0,"----------------------------------------");
            return true;
        } else {
            log(1, "    License is not valid.    ");
            log(1, " Disabling the plugin! ");
            log (0, " ");
            log(0, "      uLeaderboards      ");
            log(0,"----------------------------------------");

            Bukkit.getScheduler().cancelTasks(plugin);
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }
    }

    private Type l(){
        String rand = t(UUID.randomUUID().toString());
        String sKey = t(securityKey);
        String key  = t(licenseKey);

        try{
            URL url = new URL(validationServer+"?v1="+enc(rand, sKey)+"&v2="+enc(rand, key)+"&pl="+plugin.getName());
            if(debug) System.out.println("RequestURL -> "+url.toString());
            Scanner s = new Scanner(url.openStream());
            if(s.hasNext()){
                String response = s.next();
                s.close();
                try{
                    return Type.valueOf(response);
                }catch(IllegalArgumentException exc){
                    String respRand = enc(enc(response, key), sKey);
                    if(rand.substring(0, respRand.length()).equals(respRand)) return Type.VALID;
                    else return Type.WRONG_RESPONSE;
                }
            }else{
                s.close();
                return Type.PAGE_ERROR;
            }
        }catch(IOException exc){
            if(debug) exc.printStackTrace();
            return Type.URL_ERROR;
        }
    }
    public String o() {
        return "§I§I§§§I§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§I§§§§§§§§§§§§§§§§§§§§I§§§§§§§§§§§§§§§§§I";
    }

    private String v() {
        return "http://eylexlive.club/verify.php";
    }

    private static String enc(String s1, String s2){
        StringBuilder s0 = new StringBuilder();
        for(int i = 0; i < (Math.min(s1.length(), s2.length())) ; i++) s0.append(Byte.parseByte("" + s1.charAt(i)) ^ Byte.parseByte("" + s2.charAt(i)));
        return s0.toString();
    }

    private enum LogType{
        NORMAL, LOW, NONE;
    }

    private enum Type {
        WRONG_RESPONSE, PAGE_ERROR, URL_ERROR, KEY_OUTDATED, KEY_NOT_FOUND, NOT_VALID_IP, INVALID_PLUGIN, VALID;
    }

    private String t(String s){
        byte[] bytes = s.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary.toString();
    }
    private void log(int type, String message){
        if(logType == LogType.NONE || ( logType == LogType.LOW && type == 0 )) return;
        System.out.println(message);
    }

    public License setConsoleLog(LogType logType){
        this.logType = logType;
        return this;
    }

    public License debug(){
        debug = true;
        return this;
    }
}
