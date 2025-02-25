#include "atoms.h"

int stop = 0;

struct message {
    long mtype;
    int mvalue;
};

int create_queue(int a){
    struct message msg;
	if (a==0){
		int semid_attivatore = semget(SEM_KEY_ATTIVATORE, 1, IPC_CREAT | 0666);
	    if (semid_attivatore == -1) {
	        perror("Error: Semaphore creating failed\n");
	        exit(EXIT_FAILURE);
	    }

	    if (semctl(semid_attivatore, 0, SETVAL, 1) == -1) {
	        perror("Error: Setting activator semaphore value failed\n");
	        exit(EXIT_FAILURE);
	    }
	}

	int msgid = msgget(MSG_KEY, 0666 | IPC_CREAT);

	msg.mtype = 1;
    msg.mvalue = -2;


    if (msgsnd(msgid, &msg, sizeof(int), 0) == -1) {
        perror("Errore nell'invio del messaggio");
        return -1;
    }
    return 0;
}

void handle_signal(int sig) {
    struct message msg;
	int msgid = msgget(MSG_KEY, 0666);
    msgrcv(msgid, &msg, sizeof(int), 1, 0);
    if (msg.mvalue<0) msgrcv(msgid, &msg, sizeof(int), 1, 0);
    switch(msg.mvalue){
    	case 1:
        	// MELTDOWN
            stop = 1;
        	break;
    	case 2:
        	// EXPLODE	
            stop = 2;
            break;
    }
}



int main(int argc, char *argv[], char *envp[]){
	int n_atomico;
	char s_n_atomico[12];
	const int SIZE = sizeof(int);
    pid_t pgids[N_ATOMI_INIT];
    pid_t pid_alim, pid_atti;
	printf("\nSTART\n");
	create_queue(0);
	signal(SIGTERM, handle_signal);
	// Creazione dei semafori che daranno il via alla simulazione e quindi ai processi attivatore e alimentatore

	int semid_start = semget(SEM_KEY_START, 2, IPC_CREAT | 0666);
    if (semid_start == -1) {
        perror("Error: Semaphore creating failed\n");
        exit(EXIT_FAILURE);
    }


    // Inizializzazione dei semafori a 0 (chiuso)

    if (semctl(semid_start, 0, SETVAL, 0) == -1) {
        perror("Error: Setting activator semaphore value failed\n");
        exit(EXIT_FAILURE);
    }

    if (semctl(semid_start, 1, SETVAL, 0) == -1) {
        perror("Error: Setting alimentator semaphore value failed\n");
        exit(EXIT_FAILURE);
    }


    // Creazione della shared memory per il valore dell'energia

	int shmid = shmget(SHM_KEY, SIZE ,IPC_CREAT | 0666); 
	if(shmid == -1){
		perror("Error: shared memory create failure\n");
		exit(EXIT_FAILURE);
	}


	// Settaggio del valore iniziale dell'energia

	int *energy = (int *)shmat(shmid, NULL, 0); 
	*energy = 200;


	int shmid_statistiche = shmget(SHM_KEY_STATISTICHE, sizeof(struct Stats) ,IPC_CREAT | 0666); 
	if(shmid_statistiche == -1){
		perror("Error: shared memory create failure\n");
		exit(EXIT_FAILURE);
	}


	// Settaggio del valore iniziale dell'energia

	struct Stats *statistiche = {0};
	statistiche = (struct Stats *)shmat(shmid_statistiche, NULL, 0); 



	/*
	if (shmdt(energy) == -1) {	// unlinking dalla memoria condivisa del master 
        perror("Error: shared memory detach failure\n");
        exit(EXIT_FAILURE);
    }
    */

	

	// Creazione del processo che eseguirà l'attivatore
	
	pid_t pid = fork();
	switch(pid){
			case 0:
				execve("./attivatore", argv, envp);
				perror("Error: execve for 'attivatore.c' is not working\n");
				break;
			case -1: 
				stop = 1;
				break;
			default: 
				break;
	}
	if (stop == 0){
		pid_atti = pid;

		printf("\nMaster: Attivatore creato!\n");


		// Creazione del processo che eseguirà l'alimentatore

		pid = fork();
		switch(pid){
				case 0:
					execve("./alimentatore", argv, envp);
					perror("Error: execve for 'alimentatore.c' is not working\n");
					break;
				case -1: 
				stop = 1;
					break;
				default: 
					break;
		}	
		if (stop == 0){
			pid_alim = pid;

			printf("\nMaster: Alimentatore creato!\n");



			// Sleep inserita per evitare che i processi atomo vengano creati prima dell'effettiva creazione
			// e settaggio a chiuso del semaforo dell'attivatore. Altrimenti potrebbero cercare di accedere
			// ad un semaforo non ancora creato dall'attivatore
			sleep(1);

			// Creazione dei primi N_ATOMI_INIT atomi

			srand(time(NULL));
			for(int i = 0; i < N_ATOMI_INIT && stop == 0; i++){
				n_atomico = rand()%N_ATOM_MAX+1;
				pid = fork();
				switch(pid){
					case -1: 
						stop = 1;
						break;
					case 0:
						setpgid(0, 0);
						envp[0] = NULL;
						argv[0] = PATH_ATOMS;
						snprintf(s_n_atomico, sizeof(s_n_atomico), "%d", n_atomico);
						argv[1] = s_n_atomico;
						execve(argv[0], argv, envp);
						perror("Error: execve 'atomo.c' is not working\n");
						break;
					default:
						setpgid(pid, pid); 
		            	pgids[i] = pid;
						break;
				}
			}
			if (stop == 0){

				printf("\nMaster: Creati %d atomi!\n", N_ATOMI_INIT);




				// Seettaggio del semaforo a 1 (aperto), via alla simulazione

			    if (semctl(semid_start, 0, SETVAL, 1) == -1) {
			        perror("Error: Setting activator semaphore value failed\n");
			        exit(EXIT_FAILURE);
			    }

			    if (semctl(semid_start, 1, SETVAL, 1) == -1) {
			        perror("Error: Setting alimentator semaphore value failed\n");
			        exit(EXIT_FAILURE);
			    }


			    // Via alla stampa delle statistiche ogni secondo

			    int k = 0;
			    while((k < SIM_DURATION) && (stop == 0)){
					*energy -= ENERGY_DEMAND;
					statistiche->energy_consumed_last_second += ENERGY_DEMAND;
					statistiche->total_energy_consumed += ENERGY_DEMAND;
					if (*energy<0) stop = 3;

					printf("\n-------------------------------------\n");
					printf("\nStatistiche:\n");
					printf("ENERGIA DISPONIBILE: %d\n", *energy);
				    printf("Numero di attivazioni (totali): %d\n", statistiche->total_activations);
				    printf("Numero di attivazioni (ultimo secondo): %d\n", statistiche->activations_last_second);
				    printf("Numero di scissioni (totali): %d\n", statistiche->total_splits);
				    printf("Numero di scissioni (ultimo secondo): %d\n", statistiche->splits_last_second);
				    printf("Energia prodotta (totale): %d\n", statistiche->total_energy_produced);
				    printf("Energia prodotta (ultimo secondo): %d\n", statistiche->energy_produced_last_second);
				    printf("Energia consumata (totale): %d\n", statistiche->total_energy_consumed);
				    printf("Energia consumata (ultimo secondo): %d\n", statistiche->energy_consumed_last_second);
				    printf("Scorie prodotte (totali): %d\n", statistiche->total_waste_produced);
				    printf("Scorie prodotte (ultimo secondo): %d\n", statistiche->waste_produced_last_second);
				    printf("\n-------------------------------------\n");
		    		fflush(stdout);

				    // Resetta le statistiche relative all'ultimo secondo
				    statistiche->activations_last_second = 0;
				    statistiche->splits_last_second = 0;
				    statistiche->energy_produced_last_second = 0;
				    statistiche->energy_consumed_last_second = 0;
				    statistiche->waste_produced_last_second = 0;

					k++;
					if (stop == 0) sleep(1);
			    }
			}
		}
	}
	
    switch(stop){
    	case 0:
    		printf("\nMaster: TIMEOUT\n");
    		break;
    	case 1:
    		printf("\nMaster: MELTDOWN\n");
    		break;
    	case 2:
    		printf("\nMaster: EXPLODE\n");
    		break;
    	case 3:
    		printf("\nMaster: BLACKOUT\n");
    		break;
    	default:
    		printf("\nMaster: Qualcosa è andato storto\n");
    		break;
    }



    // Termino tutti i gruppi di atomi con primi padri gli N_ATOMI_INIT iniziali

    for (int i = 0; i < N_ATOMI_INIT; i++) {
        kill(-pgids[i], SIGINT); 
    }
    printf("\nMaster: terminati tutti gli atomi\n");
    create_queue(1);
    kill(pid_atti, SIGTERM);
    kill(pid_alim, SIGTERM);

	sleep(2);
	do {
		stop = 0;
		pid = fork();
		switch(pid){
				case 0:
					execve("./clear", argv, envp);
					perror("Error: execve for 'clear.c' is not working\n");
					break;
				case -1: 
					stop = 1;
					break;
				default: 
					break;
		}
	}while(stop == -1);
	printf("\nPulizia degli strumenti completata.\n");	

	if (stop>=0 && stop<=3){
		printf("\nMaster: Simulazione terminata correttamente.\n");
		exit(EXIT_SUCCESS);
	}else{
		printf("\nMaster: Simulazione terminata forzatamente.\n");
		exit(EXIT_FAILURE);
	}
}