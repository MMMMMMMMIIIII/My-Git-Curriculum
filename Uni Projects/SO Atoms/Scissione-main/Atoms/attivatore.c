#include "atoms.h"


int stop = 0;

void handle_signal(int sig){
	stop = 1;
}


int main(int argc, char *argv[], char *envp[]){
	
	int sem_start;
    struct sembuf sem_op;

	// Creazione del semaforo che gestirà la concorrenza nella modifica dell'energia degli atomi   
    
    int semid = semget(SEM_KEY, 1, IPC_CREAT | 0666); 
	if (semid == -1) {
        perror("Error: Semaphore creating failed\n");
        exit(EXIT_FAILURE);
    }
    
    // Settaggio del semaforo per l'energia a 0 (chiuso)
    
    if (semctl(semid, 0, SETVAL, 0) == -1) {
        perror("Error: Setting semaphore value to open failed\n");
        exit(EXIT_FAILURE);
    }

    int shmid_statistiche = shmget(SHM_KEY_STATISTICHE, sizeof(struct Stats) , 0666); 
	if(shmid_statistiche == -1){
		perror("Error: shared memory create failure\n");
		exit(EXIT_FAILURE);
	}

    struct Stats *statistiche = (struct Stats *)shmat(shmid_statistiche, NULL, 0);

    // Ottenere l'ID del semaforo per l'attesa

    sem_start = semget(SEM_KEY_START, 2, 0666);
    if (sem_start == -1) {
        perror("Error: Semaphore not found\n");
        exit(EXIT_FAILURE);
    }


    // Inizializzazione della struttura sembuf per l'operazione di attesa

    sem_op.sem_num = 0;  
    sem_op.sem_op = -1; 
    sem_op.sem_flg = 0; 


    // Attendere finché il semaforo non è disponibile (valore > 0)

    printf("\nAttivatore: Pronto alla partenza. In attesa del via del master.\n");
    if (semop(sem_start, &sem_op, 1) == -1) {
        perror("Error: semop failed\n");
        exit(EXIT_FAILURE);
    }

    printf("\nAttivatore: Partito!\n");

    signal(SIGTERM, handle_signal);

    sleep(1);

    // Ciclo che, ininterrottamente, ogni STEP_ATTIVVATORE secondi, aprirà il semaforo per N_ATOM_STARTING.
    // Il ciclo verrà poi interrotto dal master quando esso riceverà uno dei segnali di terminazione.

    while(!stop){
    	if (semctl(semid, 0, SETVAL, N_ATOM_STARTING) == -1) {
	        perror("Error: Setting semaphore value to open failed\n");
	        exit(EXIT_FAILURE);
	    }
		statistiche->total_activations += N_ATOM_STARTING;
		statistiche->activations_last_second += N_ATOM_STARTING;
	    sleep(STEP_ATTIVATORE);
    }
	printf("\nAttivatore: terminato dal master.\n");
	exit(EXIT_SUCCESS);
}