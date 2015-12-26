package org.hamsters.netty_test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for working with redirect url.
 * Maybe should be replaced with actual url parser
 */
public class RedirectURL {

    private final String address;
    private final boolean valid;

    public RedirectURL(String uri) {
        Pattern pat = Pattern.compile("/redirect\\?to=(.+)$");
        Matcher mat = pat.matcher(uri);
        valid = mat.matches();
        address = mat.group(1);
    }

    public boolean isValid() {
        return valid;
    }

    public String getAddress() {
        return address;
    }
}
