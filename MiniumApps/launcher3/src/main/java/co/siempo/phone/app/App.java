package co.siempo.phone.app;

import java.io.Serializable;

public class App implements Serializable {

    public String packageName;
    public String displayName;
    public boolean isWorkApp;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof App)) {
            return false;
        }
        App other = (App) o;

        return packageName.equals(((App) o).packageName) && isWorkApp == other.isWorkApp;
    }
}
