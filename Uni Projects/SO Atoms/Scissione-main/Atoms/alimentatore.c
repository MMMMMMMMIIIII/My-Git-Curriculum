#include "atoms.h"


struct msgbuff {
    long mtype;
    int mvalue;
};


struct message {
    long mtype;
    int mvalue;
};

int send_error(int error){
	int msgid;
    struct msgbuff msg;

    // Ottenere l'ID della coda di messaggi con la chiave specificata
    msgid = msgget(MSG_KEY, 0666 | IPC_CREAT);
    if (msgid == -1) {
        perror("Errore nella creazione della coda di messaggi");
        return -1;
    }
    msgrcv(msgid, &msg, sizeof(int), 1, 0);

    msg.mtype = 1;
    msg.mvalue = error;


    if (msgsnd(msgid, &msg, sizeof(int), 0) == -1) {
        perror("Errore nell'invio del messaggio");
        return -1;
    }

    kill(getppid(), SIGTERM);

    return 0;
}

int stop = 0;
pid_t *pgids = NULL;
int atom_count = 0;
int capacity = N_NUOVI_ATOMI * 10;
int e;

void terminate_atoms() {
    printf("\nAlimentatore: terminazione di tutti gli atomi...\n");
    for (int i = 0; i < atom_count; i++) {
        kill(-pgids[i], SIGINT);
    }

    for (int i = 0; i < atom_count; i++) {
        waitpid(-pgids[i], NULL, 0);
    }

    printf("\nAlimentatore: tutti gli atomi sono stati terminati.\n");
}

void handle_signal(int sig) {
	struct message msg;
	int msgid = msgget(MSG_KEY, 0666);
	msgrcv(msgid, &msg, sizeof(int), 1, 0);
    if (msg.mvalue==-1) {
		kill(getppid(), SIGTERM);
	}else{
		setpriority(getpid(), 0, -10);
		stop = 1;
	}
}




int main(int argc, char *argv[], char *envp[]){

	int n_atomico;
	char s_n_atomico[12];
    struct sembuf sem_op;
    int pid, sem_start;
    pgids = malloc(capacity * sizeof(pid_t));
    if (pgids == NULL) {
        perror("Error: malloc failed");
        exit(EXIT_FAILURE);
    }
	
	// Ottenere l'ID del semaforo per l'attesa 

	sem_start = semget(SEM_KEY_START, 2, 0666);
    if (sem_start == -1) {
        perror("Error: Semaphore not found\n");
        exit(EXIT_FAILURE);
    }


    // Inizializzazione della struttura sembuf per l'operazione di attesa

    sem_op.sem_num = 1;  
    sem_op.sem_op = -1; 
    sem_op.sem_flg = 0; 


    // Attendere finché il semaforo non è disponibile (valore > 0)

    printf("\nAlimentatore: Pronto alla partenza. In attesa del via del master.\n");
    if (semop(sem_start, &sem_op, 1) == -1) {
        perror("Error: semop failed\n");
        exit(EXIT_FAILURE);
    }

    printf("\nAlimentatore: Partito!\n");

    sleep(1);


    signal(SIGTERM, handle_signal);

    // Ciclo che, ininterrottamente, ogni STEP_ALIMENTATORE secondi, creerà N_NUOVI_ATOMI atomi.
    // Il ciclo verrà poi interrotto dal master quando esso riceverà uno dei segnali di terminazione.

    while(!stop){


		// Creazione di N_NUOVI_ATOMI atomi

		srand(time(NULL));
		for(int i = 0; i < N_NUOVI_ATOMI; i++){
			n_atomico = rand()%N_ATOM_MAX+1;
			pid = fork();
			switch(pid){
				case -1: 
					e = send_error(1);
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
					if (atom_count >= capacity) {
		                capacity += (N_NUOVI_ATOMI*5);
		                pgids = realloc(pgids, capacity * sizeof(int));
		                if (pgids == NULL) {
		                    perror("realloc fallita");
		                    exit(EXIT_FAILURE);
		                }
		            }
		            setpgid(pid, pid);
		            pgids[atom_count++] = pid;
					break;
			}
		}

	//	printf("\nAlimentatore: Creati %d atomi!\n", N_NUOVI_ATOMI);
	    sleep(STEP_ALIMENTATORE);
    }


    terminate_atoms();

    printf("\nAlimentatore: terminato dal master.\n");
    exit(EXIT_SUCCESS);
}