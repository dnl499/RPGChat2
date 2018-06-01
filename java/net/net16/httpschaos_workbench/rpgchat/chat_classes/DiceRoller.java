package net.net16.httpschaos_workbench.rpgchat.chat_classes;

import java.util.Random;

/**
 * Created by Daniel on 24/05/2018.
 *
 */

class DiceRoller {
    static String roll(int ndices, int nfaces, int modifier, boolean greater, boolean lower, boolean indv, int comp) {
        StringBuilder out = new StringBuilder("Rolou: ");

        Random rd = new Random();
        int t = modifier;

        for (int i = 0; i < ndices; i++) {
            int r = rd.nextInt(nfaces)+1;
            t += r;
            if (ndices <= 20 && r == nfaces) out.append("<font color=#009DFF>").append(String.valueOf(r)).append("</font>; ");
            else if (ndices <= 20 && r == 1) out.append("<font color=#FF0000>").append(String.valueOf(r)).append("</font>; ");
            else if (ndices <= 20 && indv && r > comp) out.append("<font color=#3FFF3F>").append(String.valueOf(r)).append("</font>; ");
            else if (ndices <= 20 && indv && r < comp) out.append("<font color=#FFEE00>").append(String.valueOf(r)).append("</font>; ");
            else if (ndices <= 20) out.append(String.valueOf(r)).append("; ");
        }
        if (!indv) out.append("Total: ").append(String.valueOf(t));
        if (!indv && greater && t > comp) out.append(" > ").append(String.valueOf(comp)).append(" <font color=#009DFF>Sucesso!</font>");
        else if (!indv && lower && t > comp) out.append(" < ").append(String.valueOf(comp)).append(" <font color=#009DFF>Sucesso!</font>");
        else if (!indv && greater && t < comp) out.append(" > ").append(String.valueOf(comp)).append(" <font color=#FF0000>Falha!</font>");
        else if (!indv && lower && t > comp) out.append(" < ").append(String.valueOf(comp)).append(" <font color=#FF0000>Falha!</font>");

        return out.toString();
    }
}
