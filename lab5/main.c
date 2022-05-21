#include <openmpi-x86_64/mpi.h>
#include <stdlib.h>
#include <stdio.h>

#define LINESIZE 100
#define PLANESIZE LINESIZE*LINESIZE

typedef struct cellSpace {
    char* data;
    size_t xsize;
    size_t ysize;
    size_t zsize;
} cellspace_t;

void printSpace(cellspace_t sp) {
    for (size_t k = 0; k < sp.zsize; k++) {
        for (size_t j = 0; j < sp.ysize; j++) {
            for (size_t i = 0; i < sp.xsize; i++)
                if (sp.data[i + j*LINESIZE + k+PLANESIZE])
                    printf("O");
                else
                    printf("\\");
            printf("\n");
        }
        printf("\n");
    }
}

void nextGenerationUpperLevel(cellspace_t localSpace, cellspace_t bufSpace, cellspace_t upperSpace) {
    for (long j = 0; j < bufSpace.ysize; j++)
        for (long i = 0; i < bufSpace.xsize; i++) {
            int aliveNeighbours = 0;

            aliveNeighbours += upperSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1))];
            aliveNeighbours += upperSpace.data[i + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1))];
            aliveNeighbours += upperSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1))];
            aliveNeighbours += upperSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*j];
            aliveNeighbours += upperSpace.data[i + LINESIZE*j];
            aliveNeighbours += upperSpace.data[(i + 1) % localSpace.xsize + LINESIZE*j];
            aliveNeighbours += upperSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j + 1)%localSpace.ysize)];
            aliveNeighbours += upperSpace.data[i + LINESIZE*((j + 1)%localSpace.ysize)];
            aliveNeighbours += upperSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j + 1)%localSpace.ysize)];

            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE * (localSpace.zsize - 1)];
            aliveNeighbours += localSpace.data[i + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE * (localSpace.zsize - 1)];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE * (localSpace.zsize - 1)];
            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*j + PLANESIZE * (localSpace.zsize - 1)];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*j + PLANESIZE * (localSpace.zsize - 1)];
            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE * (localSpace.zsize - 1)];
            aliveNeighbours += localSpace.data[i + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE * (localSpace.zsize - 1)];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE * (localSpace.zsize - 1)];

            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE * (localSpace.zsize - 2)];
            aliveNeighbours += localSpace.data[i + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE * (localSpace.zsize - 2)];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE * (localSpace.zsize - 2)];
            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*j + PLANESIZE * (localSpace.zsize - 2)];
            aliveNeighbours += localSpace.data[i + LINESIZE*j + PLANESIZE * (localSpace.zsize - 2)];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*j + PLANESIZE * (localSpace.zsize - 2)];
            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE * (localSpace.zsize - 2)];
            aliveNeighbours += localSpace.data[i + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE * (localSpace.zsize - 2)];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE * (localSpace.zsize - 2)];

            if (bufSpace.data[i + LINESIZE*j]) {
                bufSpace.data[i + LINESIZE * j] = aliveNeighbours == 4 || aliveNeighbours == 5 ? 1 : 0;
            } else {
                bufSpace.data[i + LINESIZE*j] = aliveNeighbours == 5 ? 1 : 0;
            }
        }
}
void nextGenerationMidLevels(cellspace_t localSpace, cellspace_t bufSpace) {
    for (long k = 1; k < bufSpace.zsize - 1; k++)
        for (long j = 0; j < bufSpace.ysize; j++)
            for (long i = 0; i < bufSpace.xsize; i++) {
                int aliveNeighbours = 0;

                aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE*(k + 1)];
                aliveNeighbours += localSpace.data[i + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE*(k + 1)];
                aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE*(k + 1)];
                aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*j + PLANESIZE*(k + 1)];
                aliveNeighbours += localSpace.data[i + LINESIZE*j + PLANESIZE*(k + 1)];
                aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*j + PLANESIZE*(k + 1)];
                aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE*(k + 1)];
                aliveNeighbours += localSpace.data[i + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE*(k + 1)];
                aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE*(k + 1)];

                aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE*k];
                aliveNeighbours += localSpace.data[i + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE*k];
                aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE*k];
                aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*j + PLANESIZE*k];
                aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*j + PLANESIZE*k];
                aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE*k];
                aliveNeighbours += localSpace.data[i + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE*k];
                aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE*k];

                aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE*(k - 1)];
                aliveNeighbours += localSpace.data[i + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE*(k - 1)];
                aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE*(k - 1)];
                aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*j + PLANESIZE*(k - 1)];
                aliveNeighbours += localSpace.data[i + LINESIZE*j + PLANESIZE*(k - 1)];
                aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*j + PLANESIZE*(k - 1)];
                aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE*(k - 1)];
                aliveNeighbours += localSpace.data[i + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE*(k - 1)];
                aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE*(k - 1)];

                if (bufSpace.data[i + LINESIZE * j]) {
                    bufSpace.data[i + LINESIZE * j] = aliveNeighbours == 4 || aliveNeighbours == 5 ? 1 : 0;
                } else {
                    bufSpace.data[i + LINESIZE * j] = aliveNeighbours == 5 ? 1 : 0;
                }
            }
}
void nextGenerationLowerLevel(cellspace_t localSpace, cellspace_t bufSpace, cellspace_t lowerSpace) {
    for (long j = 0; j < bufSpace.ysize; j++)
        for (long i = 0; i < bufSpace.xsize; i++) {
            int aliveNeighbours = 0;

            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE];
            aliveNeighbours += localSpace.data[i + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1)) + PLANESIZE];
            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*j + PLANESIZE];
            aliveNeighbours += localSpace.data[i + LINESIZE*j + PLANESIZE];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*j + PLANESIZE];
            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE];
            aliveNeighbours += localSpace.data[i + LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize+ LINESIZE*((j + 1)%localSpace.ysize) + PLANESIZE];

            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1))];
            aliveNeighbours += localSpace.data[i + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1))];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1))];
            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*j];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*j];
            aliveNeighbours += localSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j + 1)%localSpace.ysize)];
            aliveNeighbours += localSpace.data[i + LINESIZE*((j + 1)%localSpace.ysize)];
            aliveNeighbours += localSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j + 1)%localSpace.ysize)];

            aliveNeighbours += lowerSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1))];
            aliveNeighbours += lowerSpace.data[i + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1))];
            aliveNeighbours += lowerSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j - 1) >= 0 ? j - 1 : (localSpace.ysize - 1))];
            aliveNeighbours += lowerSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*j];
            aliveNeighbours += lowerSpace.data[i + LINESIZE*j];
            aliveNeighbours += lowerSpace.data[(i + 1) % localSpace.xsize + LINESIZE*j];
            aliveNeighbours += lowerSpace.data[(i - 1) >= 0 ? i - 1 : (localSpace.xsize - 1) + LINESIZE*((j + 1)%localSpace.ysize)];
            aliveNeighbours += lowerSpace.data[i + LINESIZE*((j + 1)%localSpace.ysize)];
            aliveNeighbours += lowerSpace.data[(i + 1) % localSpace.xsize + LINESIZE*((j + 1)%localSpace.ysize)];

            if (bufSpace.data[i + LINESIZE*j]) {
                bufSpace.data[i + LINESIZE * j] = aliveNeighbours == 4 || aliveNeighbours == 5 ? 1 : 0;
            } else {
                bufSpace.data[i + LINESIZE*j] = aliveNeighbours == 5 ? 1 : 0;
            }
        }
}

int main(int argc, char** argv) {
    int size, rank, prev, next;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    unsigned int steps = 5;
    cellspace_t mainSpace, localSpace, bufSpace, upperSpace, lowerSpace;
    if (rank == 0) {
        mainSpace = (cellspace_t) {calloc(PLANESIZE*LINESIZE, sizeof(char)),
                                   LINESIZE,
                                   LINESIZE,
                                   LINESIZE};
    }

    int *sendcounts, *displaces, extraLines;
    sendcounts = calloc(size, sizeof(*sendcounts));
    displaces = calloc(size, sizeof(*sendcounts));
    extraLines = LINESIZE % size;

    int i = 0, absoluteDisplace = 0;
    for (; i < extraLines; i++) {
        sendcounts[i] = PLANESIZE * (LINESIZE/size + 1);
        displaces[i] = absoluteDisplace;
        absoluteDisplace += sendcounts[i];
    }

    for (; i < size; i++) {
        sendcounts[i] = PLANESIZE * (LINESIZE/ size);
        displaces[i] = absoluteDisplace;
        absoluteDisplace += sendcounts[i];
    }

    localSpace = (cellspace_t) { calloc(sendcounts[rank], sizeof(char)),
                                        LINESIZE,
                                        LINESIZE,
                                 sendcounts[rank]/(PLANESIZE)};

    bufSpace = (cellspace_t) { calloc(sendcounts[rank], sizeof(char)),
                               LINESIZE,
                               LINESIZE,
                               sendcounts[rank]/(PLANESIZE)};

    upperSpace = (cellspace_t) { calloc(PLANESIZE, sizeof(char)),
                                 LINESIZE,
                                 LINESIZE,
                                 1};

    lowerSpace = (cellspace_t) { calloc(PLANESIZE, sizeof(char)),
                                 LINESIZE,
                                 LINESIZE,
                                 1};

    MPI_Request reqScatter, reqNextRecv, reqPrevRecv, reqNextSend, reqPrevSend;
    MPI_Iscatterv(mainSpace.data, sendcounts, displaces, MPI_CHAR,
                  localSpace.data, sendcounts[rank], MPI_CHAR,
                  0, MPI_COMM_WORLD, &reqScatter);
    next = (rank + 1) % size;
    prev = (rank - 1) >= 0 ? rank - 1 : size - 1;
    MPI_Irecv(upperSpace.data, PLANESIZE, MPI_CHAR, next, 0, MPI_COMM_WORLD, &reqNextRecv);
    MPI_Irecv(lowerSpace.data, PLANESIZE, MPI_CHAR, prev, 0, MPI_COMM_WORLD, &reqPrevRecv);

    MPI_Wait(&reqScatter, MPI_STATUS_IGNORE);

    MPI_Isend(localSpace.data, PLANESIZE, MPI_CHAR, prev, 0, MPI_COMM_WORLD, &reqPrevSend);
    MPI_Isend(&(localSpace.data[PLANESIZE * (localSpace.zsize - 1)]), PLANESIZE, MPI_CHAR, next, 0, MPI_COMM_WORLD, &reqNextSend);

    while (steps != 0) {

        MPI_Wait(&reqPrevRecv, MPI_STATUS_IGNORE);
        nextGenerationLowerLevel(localSpace, bufSpace, lowerSpace);
        MPI_Irecv(lowerSpace.data, PLANESIZE, MPI_CHAR, prev, 0, MPI_COMM_WORLD, &reqPrevRecv);
        //dangerous
        MPI_Isend(bufSpace.data, PLANESIZE, MPI_CHAR, prev, 0, MPI_COMM_WORLD, &reqPrevSend);

        nextGenerationMidLevels(localSpace, bufSpace);

        MPI_Wait(&reqNextRecv, MPI_STATUS_IGNORE);
        nextGenerationUpperLevel(localSpace, bufSpace, upperSpace);
        MPI_Irecv(upperSpace.data, PLANESIZE, MPI_CHAR, next, 0, MPI_COMM_WORLD, &reqNextRecv);
        //dangerous
        MPI_Isend(&(bufSpace.data[PLANESIZE * (localSpace.zsize - 1)]), PLANESIZE, MPI_CHAR, next, 0, MPI_COMM_WORLD, &reqNextSend);

        char* t = localSpace.data;
        localSpace.data = bufSpace.data;
        bufSpace.data = t;
        steps--;
    }

    MPI_Gatherv(localSpace.data, sendcounts[rank], MPI_CHAR,mainSpace.data, sendcounts, displaces, MPI_CHAR, 0, MPI_COMM_WORLD);
    if (rank == 0) printSpace(mainSpace);

    free(sendcounts);
    free(displaces);
    if (rank == 0) free(mainSpace.data);
    free(localSpace.data);
    free(bufSpace.data);
    free(upperSpace.data);
    free(lowerSpace.data);
}
