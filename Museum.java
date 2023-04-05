// Andrew Murphy
// 010936341
import java.util.Random;
import java.util.concurrent.*;

	class Visitor implements Runnable {
		private int num, waitTime;
		private final Random generator = new Random();
		private Semaphore waitingRoom, dinosaurRoom, zoologyRoom, giftshopRoom, mutexWaitingRoom, mutexDinosaurRoom, mutexZoologyRoom, mutexGiftshopRoom;

    Visitor(int num, int waitTime, Semaphore waitingRoom, Semaphore dinosaurRoom, Semaphore zoologyRoom, Semaphore giftshopRoom,
	 Semaphore mutextWaitingRoom, Semaphore mutexDinosaurRoom, Semaphore mutexZoologyRoom, Semaphore mutexGiftshopRoom) {
		this.num = num; // Number of visitors
		this.waitTime = waitTime;
		this.waitingRoom = waitingRoom;
		this.dinosaurRoom = dinosaurRoom;
		this.zoologyRoom = zoologyRoom;
		this.giftshopRoom = giftshopRoom;
		this.mutexWaitingRoom = mutextWaitingRoom;
		this.mutexDinosaurRoom = mutexDinosaurRoom;
		this.mutexZoologyRoom = mutexZoologyRoom;
		this.mutexGiftshopRoom = mutexGiftshopRoom;
    }
	
	// These are all initializing visitor count in each room to 0
	static class WaitingVisitors {
		static int count = 0;
	}
	static class DinoWatching {
	    static int count = 0;
	}
	static class AnimalEnjoying {
		static int count = 0;
	}
	static class BuyingGifts{
		static int count = 0;
	}
	
    // Waiting Room
    private void getWaitingRoom() {
		System.out.println("Visitor " + num + " enters the system");
		try{
			Thread.sleep(waitTime);
			waitingRoom.acquire();
		} catch(InterruptedException e){
			e.printStackTrace();
		}

		mutexWaitingRoom.release();

		try{
			mutexWaitingRoom.acquire();
		} catch(InterruptedException e){	
		}

		WaitingVisitors.count++;
		System.out.println("\tVisitor " + num + " enters the waiting area and is waiting to enter the museum. There are " + WaitingVisitors.count + " waiting");
    }
    // Dinosaur room
    private void getDinosaurRoom() {
		try{
			dinosaurRoom.acquire(); // Acquire dinosaur room
		} catch(InterruptedException e){	
		}

		try{
			mutexDinosaurRoom.acquire(); // Acquire dinosaur room mutex
		} catch(InterruptedException e){	
		}

		waitingRoom.release();
		WaitingVisitors.count--; // Reduce number of waiting visitors
		DinoWatching.count++; // Increase number of visitors in dinosaur room
		System.out.println("\t\tVisitor " + num + " enters dinosaur room. There are " + DinoWatching.count + " watching dinosaurs!");
		mutexDinosaurRoom.release();

		try{
			int time = generator.nextInt(1001); // Ten units of time
			Thread.sleep(time);
		} catch(InterruptedException e){
		}
    }
    // Zoology Room
    private void getZoologyRoom() {	
		try{
			zoologyRoom.acquire(); // Acquire zoology room
		} catch(InterruptedException e){
		}

		try{
			mutexZoologyRoom.acquire(); // Acquire zoology room mutex
		} catch(InterruptedException e){
		}

		dinosaurRoom.release();
		DinoWatching.count--; // Reduce number of visitors in dinosaur room
		AnimalEnjoying.count++; // Increase number of visitors in zoology room
		System.out.println("\t\t\tVisitor " + num + " enters zoology room. There are " + AnimalEnjoying.count + " enjoying animals!");
		mutexZoologyRoom.release();
		
		try{
			int time = generator.nextInt(1501); // 15 units of time
			Thread.sleep(time);
		} catch(InterruptedException e){
		}
    }
    // Gift Room
    private void getGiftRoom() {
		try{
			giftshopRoom.acquire(); // Acquire gift shop room
		} catch(InterruptedException e){
		}
	
		try{
			mutexGiftshopRoom.acquire(); // Acquire gift shop room mutex
		} catch(InterruptedException e){
		}
	
		zoologyRoom.release();
		AnimalEnjoying.count--; // Reduce number of visitors in the zoology room
		BuyingGifts.count++; // Increase number of visitors in gift room
		System.out.println("\t\t\t\tVisitor " + num + " enters gift room. There are " + BuyingGifts.count + " looking to buy!");
		mutexGiftshopRoom.release();

		try{
			int time = generator.nextInt(3001); // 30 units of time
			Thread.sleep(time);
		} catch(InterruptedException e){
		}
    }
    // Exit Museum
    private void exitMuseum() {
		giftshopRoom.release();
		BuyingGifts.count--;
		System.out.println("\t\t\t\t\tVisitor " + num + " exits the system.");
	}
    // Run method
    public void run() {
		// Calls all of the rooms and exit museum methods
		getWaitingRoom();
		getDinosaurRoom();
		getZoologyRoom();
		getGiftRoom();
		exitMuseum();
    }
    }
    // Museum Class
    public class Museum {
		// Creates a random generator
		private final static Random generator = new Random();
		static int time = 100;

		public static void main(String args[]) {
			int sleep = 0;
			int visitors = 0;

			if(args.length == 2) {
				sleep = (Integer.parseInt(args[0]) * time); // Gets sleep time
				visitors = Integer.parseInt(args[1]); // Gets number of visitors
			}
			else {
				System.out.println("Error");
				System.exit(0);
			}

			int maxWaiting = 40; // 40 people
			int maxDino = maxWaiting / 2; // 20 people
			int maxZoo = (maxDino + maxWaiting) * 5/12; // 25 people
			int maxGiftshop = maxWaiting * 3/4; // 30 people

			Visitor visitor;
			Semaphore waitingRoom, dinosaurRoom, zoologyRoom, giftshopRoom, mutexWaitingRoom, mutexDinosaurRoom, mutexZoologyRoom, mutexGiftshopRoom;
			// Sets new Semaphores
			waitingRoom = new Semaphore(maxWaiting);
			dinosaurRoom = new Semaphore(maxDino);
			zoologyRoom = new Semaphore(maxZoo);
			giftshopRoom = new Semaphore(maxGiftshop);
			mutexWaitingRoom = new Semaphore(1);
			mutexDinosaurRoom = new Semaphore(1);
			mutexZoologyRoom = new Semaphore(1);
			mutexGiftshopRoom = new Semaphore(1);

			for(int i = 0; i < visitors; i++){
				int wait = generator.nextInt(2000);
				// Sets visitor parameters
				visitor = new Visitor(i+1, wait, waitingRoom, dinosaurRoom, zoologyRoom, giftshopRoom, mutexWaitingRoom, mutexDinosaurRoom, mutexZoologyRoom, mutexGiftshopRoom);
				// Creates a new thread representing a visitor
				Thread thread = new Thread(visitor);
				thread.start();
			}

			try{
				Thread.sleep(sleep);
				System.exit(0);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
   