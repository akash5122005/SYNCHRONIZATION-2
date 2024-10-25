import java.util.LinkedList;

class BoundedBlockingQueue{
    private final LinkedList<String> queue = new LinkedList<>();
    private final int capacity;

    public BoundedBlockingQueue(int capacity){
        this.capacity = capacity;
    }
    public synchronized void enqueue(String item) throws InterruptedException {
        while(queue.size() == capacity){
            wait();
        }
        queue.add(item);
        System.out.println("Producer produced: "+ item);
        notifyAll();
    }
    
    public synchronized String dequeue() throws InterruptedException{
        while(queue.isEmpty()){
            wait();
        }
        String item = queue.removeFirst();
        System.out.println("Consumer consumed: "+item);
        notifyAll();
        return item;
    }
}

class Producer extends Thread{
    private final BoundedBlockingQueue queue;

    public Producer(BoundedBlockingQueue queue){
        this.queue = queue;
    }

    public void run(){
        try{
            queue.enqueue("Item 1");
            queue.dequeue();
            queue.enqueue("Item 6");
            queue.dequeue();
            queue.enqueue("Item 7");
            queue.enqueue("Item 2");
            queue.dequeue();
            queue.dequeue();
            queue.enqueue("Item 3");
            queue.enqueue("Item 8");
            queue.dequeue();
            queue.enqueue("Item 4");
            queue.dequeue();
            queue.enqueue("Item 9");
            queue.enqueue("Item 5");
            queue.enqueue("Item 10");
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}

class Consumer extends Thread{
    private final BoundedBlockingQueue queue;

    public Consumer(BoundedBlockingQueue queue){
        this.queue = queue;
    }

    public void run(){
        try{
            for(int i = 0; i <= 7; i++){
                queue.dequeue();
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args){
        BoundedBlockingQueue queue = new BoundedBlockingQueue(5);

        Producer producer = new Producer(queue);
        Consumer consumer = new Consumer(queue);

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
