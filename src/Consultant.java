import java.util.List;
import java.util.Random;

public class Consultant implements Runnable{

    private final String name;
    private final Speciality speciality;
    private final List<Patient> treatedPatients;
    private final HospitalWaitingRoom waitingRoom;

    private Random random;
    // flag to control the running state of the patient generator thread
    public volatile boolean running = true;

    public Consultant(String name, Speciality speciality, HospitalWaitingRoom waitingRoom, List<Patient> treatedPatients) {
        this.name = name;
        this.speciality = speciality;
        this.waitingRoom = waitingRoom;
        this.treatedPatients = treatedPatients;
        this.random = new Random();
    }

    public void stopConsultant() {
        running = false;
    }

    @Override
    public void run(){
        System.out.println("[Arrive Consultant] " + name + " (" + speciality + ") ready to start from " +
                speciality + " queue");

        while (running){
            try{
                Patient patient = waitingRoom.getPatient(speciality);

                if(patient != null){ // if a patient is available
                    patient.seenTime = System.currentTimeMillis();
                    System.out.println("[Start-Treatment] → " + name + " treating " + patient);

                    Thread.sleep(random.nextInt(500) + 100); // Simulate treatment time between 100ms to 600ms

                    long waitingTime = patient.seenTime - patient.getArrivalTime(); // get the waiting time

                    System.out.println("[Done-Treatment]  → " + patient + " completed by " + name +
                            " | Wait: " + waitingTime + " ms (" +
                            String.format("%.2f", waitingTime/1000.0) + " simulated hours)");

                    System.out.println("[Leave] " + patient + " from the hospital\n");

                    treatedPatients.add(patient); // record the treated patients
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                running = false;
            }
        }
        System.out.println("[Leave Consultant] " + name + " finished shift successfully! (*_*)");
    }
}