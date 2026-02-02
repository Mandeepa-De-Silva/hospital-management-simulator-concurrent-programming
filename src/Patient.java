public class Patient {

    private final int id;
    private final Speciality speciality;
    private final long arrivalTime; // to catch the time when the patient arrives
    public long seenTime = -1; // to catch the time when the patient is seen by a doctor

    public Patient(int id, Speciality speciality){
        this.id = id;
        this.speciality = speciality;
        this.arrivalTime = System.currentTimeMillis();
    }

    public Speciality getSpeciality(){
        return speciality;
    }

    public int getId(){
        return id;
    }

    public long getArrivalTime(){
        return arrivalTime;
    }

    @Override
    public String toString() {
        return "Patient " + id + " [" + speciality + "]";
    }
}
