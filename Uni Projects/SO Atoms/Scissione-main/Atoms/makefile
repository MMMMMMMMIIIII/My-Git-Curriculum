# Variabili
CC = gcc
CFLAGS = -Wvla -Wextra -Werror -D_GNU_SOURCE

# File sorgenti e oggetti
SRC_MASTER = master.c
SRC_ATOMO = atomo.c
SRC_ALIMENTATORE = alimentatore.c
SRC_ATTIVATORE = attivatore.c
SRC_CLEAR = clear.c

OBJ_MASTER = $(SRC_MASTER:.c=.o)
OBJ_ATOMO = $(SRC_ATOMO:.c=.o)
OBJ_ALIMENTATORE = $(SRC_ALIMENTATORE:.c=.o)
OBJ_ATTIVATORE = $(SRC_ATTIVATORE:.c=.o)
OBJ_CLEAR = $(SRC_CLEAR:.c=.o)



DEPS = atoms.h

# Eseguibili
EXEC = master atomo alimentatore attivatore clear

# Regole principali
all: $(EXEC)

# Regole per creare ciascun eseguibile
master: $(OBJ_MASTER)
	$(CC) $(CFLAGS) -o $@ $^

atomo: $(OBJ_ATOMO)
	$(CC) $(CFLAGS) -o $@ $^

alimentatore: $(OBJ_ALIMENTATORE)
	$(CC) $(CFLAGS) -o $@ $^

attivatore: $(OBJ_ATTIVATORE)
	$(CC) $(CFLAGS) -o $@ $^

clear: $(OBJ_CLEAR)
	$(CC) $(CFLAGS) -o $@ $^


# Regole per creare i file oggetto
%.o: %.c $(DEPS)
	$(CC) $(CFLAGS) -c -o $@ $<

# Regola di pulizia
clean:
	rm -f $(OBJ_MASTER) $(OBJ_ATOMO) $(OBJ_ALIMENTATORE) $(OBJ_ATTIVATORE) $(OBJ_CLEAR) $(EXEC)

.PHONY: all clean
