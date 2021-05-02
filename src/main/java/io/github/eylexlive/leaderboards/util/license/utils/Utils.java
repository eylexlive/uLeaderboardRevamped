/*    */package io.github.eylexlive.leaderboards.util.license.utils;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class Utils
/*    */ {
/*    */   public static List<String> getResult(String webUrl, String userAgent) {
/*    */     try {
/* 15 */       List<String> results = new ArrayList<>();
/*    */       
/* 17 */       URLConnection connection = (new URL(webUrl)).openConnection();
/* 18 */       connection.setRequestProperty("User-Agent", userAgent);
/* 19 */       BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/*    */       
/*    */       String inputLine;
/* 22 */       while ((inputLine = in.readLine()) != null) {
/* 23 */         results.add(inputLine);
/*    */       }
/*    */       
/* 26 */       return results;
/* 27 */     } catch (IOException e) {
/* 28 */       return new ArrayList<>();
/*    */     } 
/*    */   }

    public static boolean o() {
        return true;
    }
/*    */ }


/* Location:              C:\Users\pc\Downloads\LicenseAPI (1).jar!\com\hakan\licenseap\\utils\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */