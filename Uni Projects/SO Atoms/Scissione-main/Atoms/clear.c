#include "atoms.h"

int main(int argc, char *argv[], char *envp[]){
	int semid = semget(SEM_KEY_START, 2, IPC_CREAT | 0666);
	int res = semctl(semid, 0, IPC_RMID);
	while (res == -1){
		semctl(semid, 0, IPC_RMID);
	}
	semid = semget(SEM_KEY, 1, IPC_CREAT | 0666);
	res = semctl(semid, 0, IPC_RMID);
	while (res == -1){
		semctl(semid, 0, IPC_RMID);
	}
	semid = semget(SEM_KEY_ATTIVATORE, 1, IPC_CREAT | 0666);
	res = semctl(semid, 0, IPC_RMID);
	while (res == -1){
		semctl(semid, 0, IPC_RMID);
	}
	int shmid = shmget(SHM_KEY, sizeof(int), IPC_CREAT | 0666);
	res = shmctl(shmid, IPC_RMID, NULL);
	while (res == -1){
		shmctl(shmid, IPC_RMID, NULL);
	}
	int shmid_statistiche = shmget(SHM_KEY_STATISTICHE, sizeof(struct Stats), IPC_CREAT | 0666);
	res = shmctl(shmid_statistiche, IPC_RMID, NULL);
	while (res == -1){
		shmctl(shmid_statistiche, IPC_RMID, NULL);
	}
	int msgid = msgget(MSG_KEY, IPC_CREAT | 0666);
	res = msgctl(msgid, IPC_RMID, NULL);
	while (res == -1){
		msgctl(msgid, IPC_RMID, NULL);
	}
	return 0;
}