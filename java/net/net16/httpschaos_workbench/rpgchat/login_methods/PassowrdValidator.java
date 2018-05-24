package net.net16.httpschaos_workbench.rpgchat.login_methods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Daniel on 25/04/2018.
 * This class is called to validate passord. A valid passord must contain simbols, upper and lower case characters and numbers.
 * It must also be greater than 5 characters.
 * The PasswordValidator also uses a second password so it can compare to the original. If its different, it'll also invalidate.
 */

class PassowrdValidator {

    private String message;

    /**
     * This method compare two passwords to see if they are equal (Else it invalidates) and then see if they contain the requirements
     * to be an valid password. If the password is invalid, it will update the message.
     *
     * @param pass The password to validate.
     * @param conpass The passord for comparisson
     * @return Returns true if the password is valid and false in case it's not.
     */
    boolean validatePassword(String pass, String conpass) {
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

    /**
     *
     * @return The reason for the password was invalid.
     */
    String getMessage() {
        String out = message;
        message = "";
        return out;
    }
}
