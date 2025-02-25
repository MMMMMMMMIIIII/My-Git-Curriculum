#include "atoms.h"


struct msgbuff {
    long mtype;      
    int mvalue;      
};


void handle_signal(int sig) {
    switch(sig){
    	case SIGINT:
    		exit(EXIT_SUCCESS);
    		break;
    	case SIGTERM:
    		if (getpid() == getpgrp()) {
    			kill(getppid(), SIGTERM);
    		}
    		exit(EXIT_SUCCESS);
    		break;
    }
}


int send_error(int error){
	int msgid;
    struct msgbuff msg;
    struct sembuf sem_op_attivatore;

    int semid_attivatore = semget(SEM_KEY_ATTIVATORE, 1, 0666);
    if (semid_attivatore == -1) {
        perror("Error: Semaphore not found\n");
        exit(EXIT_FAILURE);
    }

    sem_op_attivatore.sem_num = 0;  
    sem_op_attivatore.sem_op = -1; 
    sem_op_attivatore.sem_flg = 0; 


    if (semop(semid_attivatore, &sem_op_attivatore, 1) == -1) {
        perror("Error: semop failed\n");
        exit(EXIT_FAILURE);
    }

    // Ottenere l'ID della coda di messaggi con la chiave specificata
    msgid = msgget(MSG_KEY, 0666);
    if (msgid == -1) {
        perror("Errore nella creazione della coda di messaggi");
        return -1;
    }

    msgrcv(msgid, &msg, sizeof(int), 1, 0);

	msg.mtype = 1;
    msg.mvalue = -1;


    if (msgsnd(msgid, &msg, sizeof(int), 0) == -1) {
        perror("Errore nell'invio del messaggio");
        return -1;
    }


    msg.mtype = 1;
    msg.mvalue = error;


    if (msgsnd(msgid, &msg, sizeof(int), 0) == -1) {
        perror("Errore nell'invio del messaggio");
        return -1;
    }
    return 0;
}

void send_signal(){
    if (getpid() == getpgrp()){
    	kill(getppid(), SIGTERM);
    }else{
    	killpg(getpgrp(), SIGTERM);
    }
}





int max(int first, int second){
	if(first>=second){
		return first;
	}else{
		return second;
	}
}




void scoria(int n_atomico, pid_t pid, struct Stats *statistiche){
	if(n_atomico <= MIN_N_ATOMICO){
	//	printf("Scoria %d\n", pid);
		statistiche->total_waste_produced += 1;
		statistiche->waste_produced_last_second += 1;
		exit(EXIT_SUCCESS);
	}
}


int main(int argc, char *argv[], char *envp[]){
	int energia;
	int n_atomico = atoi(argv[1]);
	pid_t pid = getpid();
	int shmid_statistiche = shmget(SHM_KEY_STATISTICHE, sizeof(struct Stats) , 0666); 
	if(shmid_statistiche == -1){
		perror("Error: shared memory create failure\n");
		exit(EXIT_FAILURE);
	}
    struct Stats *statistiche = (struct Stats *)shmat(shmid_statistiche, NULL, 0);
	scoria(n_atomico, pid, statistiche);
	const int SIZE = sizeof(int);
	char s_child[12];
	envp[0] = NULL;
	argv[0] = PATH_ATOMS;
	int semid;
    struct sembuf sem_op;
    int tmp = 0, e = 0;

    signal(SIGTERM, handle_signal);
    signal(SIGINT, handle_signal);
	
	// Ottenere l'ID del semaforo dell'attivatore per l'attesa

    semid = semget(SEM_KEY, 1, 0666);
    if (semid == -1) {
        perror("Error: Semaphore not found\n");
        exit(EXIT_FAILURE);
    }


    // Inizializzazione della struttura sembuf per l'operazione di attesa

    sem_op.sem_num = 0;  
    sem_op.sem_op = -1; 
    sem_op.sem_flg = 0; 


    // Attendere finché il semaforo non è disponibile (valore > 0)

    if (semop(semid, &sem_op, 1) == -1) {
        perror("Error: semop failed\n");
        exit(EXIT_FAILURE);
    }

	srand(time(NULL));
	int child1 = rand()%(n_atomico-1)+1; // gestito in questo modo poichè poi scoria() controllerà se sono scorie, non lo dobbiamo prevedere
	int child2 = n_atomico - child1;


	// Ottengo l'ID della memoria condivisa

    int shmid = shmget(SHM_KEY, SIZE, 0666);
    if (shmid == -1) {
        perror("Error: failed to get shared memory");
        exit(EXIT_FAILURE);
    }


    // Mi attacco alla memoria condivisa

    void *shared_memory = shmat(shmid, NULL, 0);
    if (shared_memory == (void *) -1) {
        perror("Error: failed to attach shared memory");
        exit(EXIT_FAILURE);
    }


    // Aggiungo l'energia rilasciata nella scissione

    int *energy = (int *)shared_memory;
    *energy += (child1*child2) - max(child1, child2);
	statistiche->total_energy_produced += (child1*child2) - max(child1, child2);
	statistiche->energy_produced_last_second += (child1*child2) - max(child1, child2);
    if (*energy>ENERGY_EXPLODE_THRESHOLD) {
    	e = send_error(2);
    	send_signal();
    }
//  printf("Energia dopo incremento: %d\n", *energy);
//  fflush(stdout);

    // Mi stacco dalla memoria condivisa
    if (shmdt(shared_memory) == -1) {
        perror("Error: failed to detach shared memory");
        exit(EXIT_FAILURE);
    }


	switch(fork()){
		case -1:
			e = send_error(1);
			send_signal();
			break;
		case 0:
			snprintf(s_child, sizeof(s_child), "%d", child1);
		//	printf("Sono atomo %d (padre) con peso %s\n", pid, s_child);
    	//	fflush(stdout);
			argv[1] = s_child;
			execve(argv[0], argv, envp);
			perror("Error: execve of the child atom failure\n");
			exit(EXIT_FAILURE);
		default:
			switch(fork()){
				case -1:
					e = send_error(1);
					send_signal();
					break;
				case 0:
					statistiche->total_splits += 1;
					statistiche->splits_last_second += 1;
					sprintf(s_child, "%d", child2);
		//			printf("Sono atomo %d (figlio) con peso %s\n", pid, s_child);
    	//			fflush(stdout);
					argv[1] = s_child;
					execve(argv[0], argv, envp);
					perror("Error: execve of the parent atom failure\n");
					exit(EXIT_FAILURE);
				default:
					break;
			}
	}



	return 0;
}