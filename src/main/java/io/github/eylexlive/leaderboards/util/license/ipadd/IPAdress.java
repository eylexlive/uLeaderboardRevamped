/*    */ package io.github.eylexlive.leaderboards.util.license.ipadd;
/*    */
/*    */ import io.github.eylexlive.leaderboards.util.license.ipadd.enums.IPType;

import java.io.BufferedReader;
/*    */ import java.io.InputStreamReader;
/*    */ import java.net.InetAddress;
/*    */ import java.net.URL;
/*    */ import java.net.UnknownHostException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IPAdress
/*    */ {
/*    */   public static String getIp(IPType ipType) {
/* 17 */     String ipAdress = "----notfound----";
/* 18 */     switch (ipType) {
/*    */       case MACHINE:
/*    */         try {
/* 21 */           ipAdress = InetAddress.getLocalHost().getHostAddress();
/* 22 */         } catch (UnknownHostException unknownHostException) {}
/*    */         break;
/*    */       
/*    */       case PUBLIC:
/*    */         try {
/* 27 */           URL url_name = new URL("http://bot.whatismyipaddress.com");
/* 28 */           BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));
/* 29 */           ipAdress = sc.readLine().trim();
/* 30 */         } catch (Exception exception) {}
/*    */         break;
/*    */     } 
/*    */     
/* 34 */     return ipAdress;
/*    */   }

            public static boolean m() {
                return true;
            }
/*    */ }