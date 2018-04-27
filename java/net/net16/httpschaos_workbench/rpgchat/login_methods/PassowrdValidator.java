package net.net16.httpschaos_workbench.rpgchat.login_methods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Daniel on 25/04/2018.
 *
 */

public class PassowrdValidator {

    private String message;

    public boolean validatePassword(String pass, String conpass) {
        boolean valid = false;

        if (!pass.isEmpty()) {
            if (!conpass.isEmpty()) {
                if (pass.equals(conpass)) {
                    if (pass.length() > 5) {

                        Pattern pattern;
                        Matcher matcher;
                        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
                        pattern = Pattern.compile(PASSWORD_PATTERN);
                        matcher = pattern.matcher(pass);

                        if (matcher.matches()) {
                            valid = true;

                        } else message = "A senha deve conter números, letras maiúsculas e minúsculas, além de caracteres especiais";
                    } else message = "Senha muito pequena";
                } else message = "Senhas não combinam";
            } else  message = "Segunda senha vazia";
        } else message = "Senha vazia";

        return valid;
    }

    public String getMessage() {
        return message;
    }
}
