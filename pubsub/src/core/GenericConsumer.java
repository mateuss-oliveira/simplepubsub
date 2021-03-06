package core;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;

public abstract class GenericConsumer<S> extends Thread {

    protected GenericResource<S> re;
    protected Map<String, PubSubCommand> commands;
    private boolean stop;

    public GenericConsumer(GenericResource<S> re) {
        this.re = re;
        commands = new HashMap<String, PubSubCommand>();
        loadCommandProperties();
        stop = false;
    }

    @SuppressWarnings({"rawtypes", "deprecation"})
    protected void loadCommandProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
            Set<String> ids = properties.stringPropertyNames();
            for (String aux : ids) {
                Class c = Class.forName(properties.getProperty(aux));
                commands.put(aux, (PubSubCommand) c.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            S str = null;

            while (((!re.isFinished()) || (re.getNumOfRegisters() != 0)) && (!stop)) {
                if ((str = re.getRegister()) != null) {
                    //fazer algo com o recurso que foi consumido
                    doSomething(str);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void stopConsumer() {
        stop = true;
    }

    protected abstract void doSomething(S str);
    public abstract String getPrimaryAddress();
    public abstract int getPrimaryPort();
    public abstract Boolean getIsPrimary();
    public abstract ArrayList<String> getBrokersList();

}