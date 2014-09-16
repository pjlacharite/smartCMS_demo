package tools;

import play.api.i18n.Lang;
import play.i18n.Messages;

public class LanguageUtil {
    public static String KEY_LANG = "lang";

    public static String message(play.mvc.Http.Session session, String text, Object... args) {
        if (session.get(KEY_LANG) != null) {
            return Messages.get(new Lang(session.get(KEY_LANG), null), text, args);
        } else {
            return Messages.get(text, args);
        }

    }

}
