public class Main {

    public static void main(String[] args) {
        Threadpool tp = new Threadpool(100);

        for (int i =  0; i < 1000; i++) {
            tp.addTask(()-> {
                System.out.println("I'm a task thread id:" + Thread.currentThread().getId());
            });
        }
    }
}
