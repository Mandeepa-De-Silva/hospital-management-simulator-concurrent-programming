import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HospitalWaitingRoom {

    // used blocking queue to thread-safe adding and removing patients FIFO basis
    private final BlockingQueue<Patient> cardiologistQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Patient> surgeonQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Patient> paediatricianQueue = new LinkedBlockingQueue<>();

    private final AtomicInteger totalPatientsArrived = new AtomicInteger(0);

    public void addPatient(Patient patient){
        try{
            BlockingQueue<Patient> queue = getQueue(patient.getSpeciality());
            queue.put(patient);

            totalPatientsArrived.incrementAndGet();

            System.out.println(
                    "[Queue] " + patient + " added to " + patient.getSpeciality() +
                            " queue. Queues → C:" + cardiologistQueue.size() + " | S:" + surgeonQueue.size() +
                            " | P:" + paediatricianQueue.size());
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.err.println("Failed to admit patient to the queue: " + patient);
        }
    }

    public Patient getPatient(Speciality speciality){
        try{
            return getQueue(speciality).take(); // wait for the patient and execute
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return null;
        }
    }

    // this method ensure to get a patient specifically based on the speciality
    public BlockingQueue<Patient> getQueue(Speciality speciality){
        return switch (speciality){
            case CARDIOLOGIST -> cardiologistQueue;
            case SURGEON -> surgeonQueue;
            case PAEDIATRICIAN -> paediatricianQueue;
        };
    }

    public int getTotalPatientsArrived() {
        return totalPatientsArrived.get();
    }
}
