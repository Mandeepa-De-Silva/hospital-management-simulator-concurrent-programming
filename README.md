# Hospital Management Simulator – Concurrent Programming

Simulating a hospital patient flow system in Java using advanced concurrency mechanisms.  
The system models real-world hospital scenarios where patients arrive unpredictably, wait in per-specialty queues, get treated by consultant threads during shifts, with smooth handover between shifts and graceful shutdown.

It demonstrates producer-consumer patterns, blocking queues for safe & fair patient handling, thread coordination, and resource safety under concurrent access.

## Core Features

- Simulates unpredictable patient arrivals over time
- Dedicated **per-specialty queues** (e.g., one queue per medical specialty)
- Consultant threads treat patients from their specialty queue
- Shift-based operation with smooth handover (no interruption of ongoing treatments)
- Graceful system shutdown using volatile flag
- Tracks patient waiting time, treatment time, and system metrics (e.g., 4-hour target waiting)
- Prevents starvation, deadlock, and resource exhaustion

## Key Concurrency Design Choices & Why

| Component                        | Choice Made                                      | Why this choice?                                                                                          | Why NOT alternatives?                                                                                           |
|----------------------------------|--------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| **Patient Queues**               | `LinkedBlockingQueue` per specialty              | Built-in blocking + FIFO ordering → ensures patients treated in arrival order (fairness).<br>Internally handles mutual exclusion & synchronization.<br>Blocks safely when empty/full → no busy-waiting, saves CPU. | Manual `synchronized` + `wait()/notify()` → higher complexity, risk of deadlock/liveness issues, error-prone.   |
| **Queue Type**                   | BlockingQueue (LinkedBlockingQueue)              | Provides **liveness** & **fairness** (FIFO).<br>Threads block efficiently when no patients → prevents CPU waste.<br>Guarantees thread-safety without extra locks. | Non-blocking queue → requires manual polling → wastes CPU, no built-in fairness/liveness.                       |
| **Shutdown Mechanism**           | `volatile boolean` flag + observed by threads    | Ensures **visibility** across threads (volatile keyword).<br>Allows graceful shutdown: consultants stop accepting new patients but finish current ones.<br>No abrupt termination. | Non-volatile flag → changes may not be visible → threads miss shutdown signal.<br>Hard shutdown → lost patients/inconsistent state. |
| **Shift Handover**               | Consultants observe flag & finish current patient | Smooth handover: current treatment completes before shift ends.<br>No interruption → realistic & safe.                 | Abrupt shift change → incomplete treatments, lost progress.                                                    |
| **Shared Resources**             | Patient counters (likely AtomicInteger)          | Thread-safe updates (atomic increments) → prevents race conditions on shared patient stats.<br>No blocking for simple counts. | Unsynchronized counters → lost updates, incorrect metrics (e.g., wrong waiting time totals).                    |
| **Patient Arrival & Processing** | Producer (arrival thread) + Consumer (consultants) | Classic producer-consumer with bounded queues → controls system load.<br>Prevents overload/starvation.                | Unbounded or no queues → system crash under high arrival rate, unfair treatment order.                          |

### Why LinkedBlockingQueue?

- **FIFO ordering** → patients treated in arrival sequence (fairness)
- **Blocking put/take** → consultants wait efficiently when no patients (no CPU spin)
- **Internal synchronization** → no need for manual locks/wait/notify
- **Unbounded by default** → handles any number of patients without capacity limits, realistic for variable arrival rates (no rejection of arrivals due to full queues)
- **Liveness & safety** → avoids deadlock/starvation compared to low-level primitives

Avoided manual `synchronized` blocks + `wait()/notify()` because:
- More complex code
- Higher risk of deadlock, liveness issues, missed signals
- Lower-level → easier to introduce bugs

## What Could Go Wrong Without Proper Concurrency?

- Race conditions on patient counters → incorrect waiting/treatment stats
- Starvation → some patients wait forever (no FIFO)
- Busy-waiting/polling → high CPU usage
- Deadlock → if manual locks used incorrectly
- Abrupt shutdown → lost/inconsistent patient states
- Overload → unlimited queue growth → memory exhaustion

## Technologies Used

- Java (`java.util.concurrent`)
- `LinkedBlockingQueue`, `volatile`, likely `AtomicInteger`, `ExecutorService` or threads
- Core Java only — no external dependencies

## How to Run

1. Clone the repository
   ```bash
   git clone https://github.com/Mandeepa-De-Silva/hospital-management-simulator-concurrent-programming.git
