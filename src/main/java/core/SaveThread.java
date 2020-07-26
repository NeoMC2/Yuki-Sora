package core;

public class SaveThread implements Runnable {

    boolean stop = false;
    private final Engine engine;

    public SaveThread(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void run() {
        while (true) {
            if (stop) {
                break;
            }
            try {
                Thread.sleep(60000 * engine.getProperties().saveSpeed);
            } catch (InterruptedException e) {
                if (engine.getProperties().debug) {
                    e.printStackTrace();
                }
            }
            if (stop) {
                break;
            }
            engine.saveProperties();
            if (engine.getDiscEngine().isRunning()) {
                engine.getDiscEngine().getFilesHandler().saveAllBotFiles();
            }
        }
    }

    public void stop() {
        stop = true;
    }
}
