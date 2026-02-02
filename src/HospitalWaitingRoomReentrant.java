import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HospitalWaitingRoomReentrant {

    // used blocking queue to thread-safe adding and removing patients FIFO basis
    private final Queue<Patient> cardiologistQueue = new ArrayDeque<>();
    private final Queue<Patient> surgeonQueue = new ArrayDeque<>();
    private final Queue<Patient> paediatricianQueue = new ArrayDeque<>();
    private final Lock lock = new ReentrantLock(true);
    private final Condition notEmpty = lock.newCondition();

    private  int totalPatientsArrived = 0;

    public void addPatient(Patient patient){
        lock.lock();
        try{
            Queue<Patient> queue = getQueue(patient.getSpeciality());
            queue.add(patient);

            totalPatientsArrived++;
            notEmpty.signalAll();

            System.out.println(
                    "[Queue] " + patient + " added to " + patient.getSpeciality() +
                            " queue. Queues → C:" + cardiologistQueue.size() + " | S:" + surgeonQueue.size() +
                            " | P:" + paediatricianQueue.size());
        }finally {
            lock.unlock();
        }
    }

    public Patient getPatient(Speciality speciality){
        lock.lock();
        try{
            Queue<Patient> queue = getQueue(speciality);
            while(queue.isEmpty()){
                notEmpty.await();
            }
            return queue.poll(); // wait for the patient and execute
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return null;
        }finally {
            lock.unlock();
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

    public int getTotalPatientsArrived() {
        lock.lock();
        try{
            return totalPatientsArrived;
        }finally {
            lock.unlock();
        }
    }
}
