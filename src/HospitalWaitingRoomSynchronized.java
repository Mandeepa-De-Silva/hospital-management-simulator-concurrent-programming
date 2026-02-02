import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HospitalWaitingRoomSynchronized {

    // used blocking queue to thread-safe adding and removing patients FIFO basis
    private final Queue<Patient> cardiologistQueue = new ArrayDeque<>();
    private final Queue<Patient> surgeonQueue = new ArrayDeque<>();
    private final Queue<Patient> paediatricianQueue = new ArrayDeque<>();

    private  int totalPatientsArrived = 0;

    public synchronized void addPatient(Patient patient){
//        try{
            Queue<Patient> queue = getQueue(patient.getSpeciality());
            queue.add(patient);

            totalPatientsArrived++;
            notifyAll();

            System.out.println(
                    "[Queue] " + patient + " added to " + patient.getSpeciality() +
                            " queue. Queues → C:" + cardiologistQueue.size() + " | S:" + surgeonQueue.size() +
                            " | P:" + paediatricianQueue.size());
//        }catch (InterruptedException e){
//            Thread.currentThread().interrupt();
//            System.err.println("Failed to admit patient to the queue: " + patient);
//        }
    }

    public synchronized Patient getPatient(Speciality speciality){
        try{
            Queue<Patient> queue = getQueue(speciality);
            while(queue.isEmpty()){
                wait();
            }
            return queue.poll(); // wait for the patient and execute
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return null;
        }
    }

    // this method ensure to get a patient specifically based on the speciality
    public Queue<Patient> getQueue(Speciality speciality){
        return switch (speciality){
            case CARDIOLOGIST -> cardiologistQueue;
            case SURGEON -> surgeonQueue;
            case PAEDIATRICIAN -> paediatricianQueue;
        };
    }

    public synchronized int getTotalPatientsArrived() {
        return totalPatientsArrived;
    }
}
