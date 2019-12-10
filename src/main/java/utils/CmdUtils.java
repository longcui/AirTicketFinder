package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class CmdUtils {

     public static String runCmd(String cmd) throws IOException {
//         ProcessBuilder builder = new ProcessBuilder(
//                 "cmd.exe", "/c", "cd \"C:\\Program Files\\Microsoft SQL Server\" && dir");
         ///C      Carries out the command specified by string and then terminates
         ProcessBuilder builder = new ProcessBuilder(
                 "cmd.exe", "/c", cmd);
         builder.redirectErrorStream(true);
         Process p = builder.start();
         BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return   r.lines().collect(Collectors.joining());
     }
}
