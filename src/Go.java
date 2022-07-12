

public class Go implements Runnable{
    private MenuScreen menuScreen;
    private Window window;
    private GameLogic gameLogic;
    private boolean running = false;
    private Thread thread;
    private Tile[][] tiles;

    public Go(){
        init();
    }

    public void init(){
        menuScreen = new MenuScreen(this);
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop(){
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        long lastTime = System.nanoTime();
        double frameLimit = 60.0;
        double ns = 1000000000 / frameLimit;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        while(running){
            long now = System.nanoTime();

            delta += (now - lastTime) / ns;
            lastTime = now;

            while(delta >= 1){
                tick();
                if(window != null) {
                    window.repaint();
                }
                delta--;
                frames++;
            }

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                // System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }

    public void tick(){
        if (window != null){
            window.tick();
            tiles = window.getTiles();
        }

        if(gameLogic != null){
            getGameLogic().tick();
        }
    }

    public void createScreen(int dim){
        window = new Window(this, dim);
        tiles = window.getTiles();
        gameLogic = new GameLogic(this, tiles, dim);
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }
}



