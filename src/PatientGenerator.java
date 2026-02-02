import java.util.Random;

public class PatientGenerator implements Runnable{

    private final HospitalWaitingRoom waitingRoom;
    private Random random = new Random();

    // flag to control the running state of the patient generator thread
    public volatile boolean running = true;

    public PatientGenerator(HospitalWaitingRoom waitingRoom){
        this.waitingRoom = waitingRoom;
    }

    public void stopPatientGenerator() {
        running = false;
    }

    @Override
    public void run(){
        int id = 1;
        while (running){
            try{
                Thread.sleep(random.nextInt(300));

                if (!running) {
                    break;
                }

                // Randomly select a speciality
                Speciality speciality = Speciality.values()[random.nextInt(3)];
                Patient patient = new Patient(id++, speciality); // Create new patient with unique ID and random speciality

                System.out.println("[Arrival] New Patient → " + patient + " requiring " + speciality);
                waitingRoom.addPatient(patient);

            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while generating patients..(-_-)");
                running =false;
            }
        }
    }
}
