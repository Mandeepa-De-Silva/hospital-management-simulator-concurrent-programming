import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HospitalWaitingRoomSemaphore {

    // used blocking queue to thread-safe adding and removing patients FIFO basis
    private final Queue<Patient> cardiologistQueue = new ArrayDeque<>();
    private final Queue<Patient> surgeonQueue = new ArrayDeque<>();
    private final Queue<Patient> paediatricianQueue = new ArrayDeque<>();

    private final Semaphore availablePatients = new Semaphore(0);

    private  int totalPatientsArrived = 0;

    public synchronized void addPatient(Patient patient){
        try{
            Queue<Patient> queue = getQueue(patient.getSpeciality());
            queue.add(patient);

            totalPatientsArrived++;

            System.out.println(
                    "[Queue] " + patient + " added to " + patient.getSpeciality() +
                            " queue. Queues → C:" + cardiologistQueue.size() + " | S:" + surgeonQueue.size() +
                            " | P:" + paediatricianQueue.size());
        }finally {
            availablePatients.release();
        }
    }

    public Patient getPatient(Speciality speciality){
        try{
            availablePatients.acquire();
            synchronized (this){
                Queue<Patient> queue = getQueue(speciality);
                return queue.poll(); // wait for the patient and execute
            }

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
