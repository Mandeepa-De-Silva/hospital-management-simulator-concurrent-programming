import java.util.ArrayList;
import java.util.List;

public class HospitalSimulator {
    public static final int daysToSimulate = 2;
    public static final int simulatedHoursMs = 1000; // 1 simulated hour = 1000 ms real time
    public static final long shiftDurationMs = 12L * simulatedHoursMs; // 12 simulated hours per shift

    public static void main(String[] args) {

        HospitalWaitingRoom waitingRoom = new HospitalWaitingRoom();
        List<Patient> treatedPatients = new ArrayList<>(); // list to record treated patients
        ShiftManager shiftManager = new ShiftManager(waitingRoom,treatedPatients);

        PatientGenerator generator = new PatientGenerator(waitingRoom);
        Thread patientGenerator = new Thread(generator, "PatientGenerator");
        patientGenerator.start();

        System.out.println("\nStarting hospital simulation with manageable shift..(*_*)");

        try {
            for (int day = 0; day < daysToSimulate; day++) {
                List<Thread> dayShift = shiftManager.startDayShift(day);
                Thread.sleep(shiftDurationMs);
                shiftManager.endShift(dayShift);

                List<Thread> nightShift = shiftManager.startNightShift(day);
                Thread.sleep(shiftDurationMs);
                shiftManager.endShift(nightShift);
            }

            System.out.println("Stopping patient admissions..(*_*)");
            generator.stopPatientGenerator();

            // wait for generator to fully stop
            patientGenerator.join();

            System.out.println("Hospital simulation completed successfully (*_*). Closing hospital...");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while changing the shift..(-_-)");
        }

        // final statistics
        long targetWaitMs = 4L * simulatedHoursMs;
        int withinTarget = 0;
        long totalWaitMs = 0;

        int totalProcessed = treatedPatients.size();
        int totalArrived = waitingRoom.getTotalPatientsArrived();
        int notTreated = totalArrived - totalProcessed;

        int remainingCardiology = waitingRoom.getQueue(Speciality.CARDIOLOGIST).size();
        int remainingSurgery = waitingRoom.getQueue(Speciality.SURGEON).size();
        int remainingPaediatric = waitingRoom.getQueue(Speciality.PAEDIATRICIAN).size();

        for (Patient patient : treatedPatients) {
            long wait = patient.seenTime - patient.getArrivalTime();
            totalWaitMs += wait; // calculate total wait time
            if (wait <= targetWaitMs) {
                withinTarget++; // count patients seen within target time
            }
        }

        double percentageWithinTarget = totalArrived > 0 ? (100.0 * withinTarget / totalArrived) : 0.0;
        double averageWaitSeconds = totalProcessed > 0 ? (totalWaitMs / 1000.0 / totalProcessed) : 0;
        double avgMinutes = averageWaitSeconds * 60.0;

        System.out.println("\n" + "=".repeat(70));
        System.out.println("                 (*_*) Hospital Statistics (*_*)");
        System.out.println("-".repeat(70));
        System.out.println("Total patients arrived          : " + totalArrived);
        System.out.println("Total patients treated          : " + totalProcessed);
        System.out.println("Total patients not treated      : " + notTreated);
        System.out.println("\n" + "-".repeat(70));
        System.out.println("                        Average statistics");
        System.out.println("-".repeat(70));
        System.out.println("Average wait time (simulated)   : " + String.format("%.2f hours (%.0f minutes)", averageWaitSeconds, avgMinutes));
        System.out.println("Patients seen within 4 hours    : " + withinTarget + " (" + String.format("%.2f%%", percentageWithinTarget) + ")");
        System.out.println("-".repeat(70));
        System.out.println("                        Remaining Queues");
        System.out.println("-".repeat(70));
        System.out.println("Cardiology queue remaining      : " + remainingCardiology);
        System.out.println("Surgery queue remaining         : " + remainingSurgery);
        System.out.println("Paediatric queue remaining      : " + remainingPaediatric);
        System.out.println("-".repeat(70));

        if (percentageWithinTarget >= 95.0) {
            System.out.println("                    NHS TARGET ACHIEVED (*_*)");
        } else {
            System.out.println("            NHS target not met – consider faster treatment or more staff (-_-)");
        }
        System.out.println("=".repeat(70));
    }
}