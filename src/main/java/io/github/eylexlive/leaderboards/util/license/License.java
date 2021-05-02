/*    */ package io.github.eylexlive.leaderboards.util.license;
/*    */
/*    */ import io.github.eylexlive.leaderboards.util.license.ipadd.IPAdress;
import io.github.eylexlive.leaderboards.util.license.ipadd.enums.IPType;
import io.github.eylexlive.leaderboards.util.license.utils.Utils;

import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class License
/*    */ {
/*    */   public static boolean has(String plugin) {
/* 15 */     String ip1 = IPAdress.getIp(IPType.MACHINE);
/* 16 */     List<String> result = Utils.getResult(has(new String[]{ip1, plugin}), "MandAPluginAccess");
/* 17 */     return (result.size() > 0 && result.get(0).contains("there_is_license"));
/*    */   }

            public static String has(String[] args) {
                return k() + "/users/check.php?ip=" + args[0] + "&plugin=" + args[1];
            }

            public static String k() {
                return "http://mvea-license.xyz";
            }
/*    */ }


/* Location:              C:\Users\pc\Downloads\LicenseAPI (1).jar!\com\hakan\licenseapi\License.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */