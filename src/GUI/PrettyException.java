package GUI;

import java.io.IOException;

public class PrettyException extends IOException {
    private final IOException e;
    private final String prettyMessage;
    public String getPrettyMessage(){
        return prettyMessage;
    }
    public IOException getE(){
        return e;
    }

    public PrettyException(IOException e, String prettyMessage){
        this.e = e;
        this.prettyMessage = prettyMessage;
    }

    public PrettyException(String prettyMessage){
        this.e = null;
        this.prettyMessage = prettyMessage;
    }
}
