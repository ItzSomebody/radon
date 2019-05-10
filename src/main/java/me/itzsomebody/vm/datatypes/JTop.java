package me.itzsomebody.vm.datatypes;

public class JTop extends JWrapper {
    private static JTop top;

    private JTop() {
    }

    public static JTop getTop() {
        return top;
    }
}
