package dataRecieve;

public class LoginPasswordWrapper {
    private String name;
    private byte[] password;

    public String getName() {
        return name;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }
}
