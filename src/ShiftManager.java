import java.util.ArrayList;
import java.util.List;

public class ShiftManager {

    private final HospitalWaitingRoom waitingRoom;
    private final List<Patient> treatedPatients;
    private int shiftNumber = 0;

    // current shift consultants
    private final List<Consultant> currentShiftConsultants = new ArrayList<>();

    public ShiftManager(HospitalWaitingRoom waitingRoom, List<Patient> treatedPatients) {
        this.waitingRoom = waitingRoom;
        this.treatedPatients = treatedPatients;
    }

    public List<Thread> startDayShift(int day) {
        shiftNumber++;
        System.out.println("\n" + "=".repeat(70));
        System.out.println("          DAY SHIFT " + (day + 1) + " STARTING (Shift #" + shiftNumber + ")");
        System.out.println("   3 Consultants starting • Continuing 24/7 concurrent operations");
        System.out.println("=".repeat(70)+ "\n");
        return startShift("Day");
    }

    public List<Thread> startNightShift(int day) {
        shiftNumber++;
        System.out.println("\n" + "=".repeat(70));
        System.out.println("          NIGHT SHIFT " + (day + 1) + " STARTING (Shift #" + shiftNumber + ")");
        System.out.println("   Handover complete • Continuing 24/7 concurrent operations");
        System.out.println("=".repeat(70)+"\n");
        return startShift("Night");
    }

    private List<Thread> startShift(String type) {
        currentShiftConsultants.clear(); // reset for new shift
        List<Thread> threads = new ArrayList<>();

        threads.add(startConsultant(
                "Dr." + type + "-Surgeon-" + shiftNumber,
                Speciality.SURGEON));

        threads.add(startConsultant(
                "Dr." + type + "-Cardiologist-" + shiftNumber,
                Speciality.CARDIOLOGIST));

        threads.add(startConsultant(
                "Dr." + type + "-Paediatrician-" + shiftNumber,
                Speciality.PAEDIATRICIAN));

        return threads;
    }

    private Thread startConsultant(String name, Speciality speciality) {
        Consultant consultant = new Consultant(
                name, speciality, waitingRoom, treatedPatients);
        currentShiftConsultants.add(consultant);
        Thread t = new Thread(consultant);
        t.start();
        return t;
    }

    public void endShift(List<Thread> threads) {
        System.out.println("[SHIFT] Ending shift...");

        for (Consultant consultant : currentShiftConsultants) {
            consultant.stopConsultant(); // genuinely stop the consultant
        }

        for (Thread thread : threads) {
            try {
                thread.join(500);
            } catch (InterruptedException ignored) {}
        }

        System.out.println("[SHIFT] Shift ended successfully (*_*)\n");
    }

}
