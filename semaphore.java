import java.util.LinkedList;
import java.util.concurrent.Semaphore;

class BoundedBuffer{
    private final LinkedList<Integer> buffer = new LinkedList<>();
    private final Semaphore emptySlots;
    private final Semaphore fullSlots;
    private final Semaphore mutex;

    public BoundedBuffer(int capacity){
        emptySlots = new Semaphore(capacity);
        fullSlots = new Semaphore(0);
        mutex = new Semaphore(1);
    }
    public void produce(int item) throws InterruptedException{
        emptySlots.acquire();
        mutex.acquire();

        buffer.add(item);
        System.out.println("Producer produced: "+ item);

        mutex.release();
        fullSlots.release();
    }
    public int consume() throws InterruptedException{
        fullSlots.acquire();
        mutex.acquire();

        int item = buffer.removeFirst();
        System.out.println("Consumer consumed: "+ item);

        mutex.release();
        emptySlots.release();
        return item;
    }
}

class Producer extends Thread{
    private final BoundedBuffer buffer;

    public Producer(BoundedBuffer buffer){
        this.buffer = buffer;
    }
    public void run(){
        try{
            for(int i = 1; i <=6; i++){
                buffer.produce(i);
                Thread.sleep(100);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
class Consumer extends Thread{
    private final BoundedBuffer buffer;

    public Consumer(BoundedBuffer buffer){
        this.buffer = buffer;
    }
    public void run(){
        try{
            for (int i= 1; i <=6; i++){
                buffer.consume();
                Thread.sleep(150);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
public class Main{
    public static void main(String[] args){
        BoundedBuffer buffer = new BoundedBuffer(3);

        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        producer.start();
        consumer.start();

        try{
            producer.join();
            consumer.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
