#include <stdio.h>
#include <openmpi-x86_64/mpi.h>
#include <stdlib.h>

void multiplyMatricies(double* A, double* B, double* C, 
        size_t rowsA, size_t colsA, size_t colsB) {
    for (size_t i = 0; i < rowsA; i++) 
        for (size_t j = 0; j < colsB; j++)
            for (size_t k = 0; k < colsA; k++) {
                C[j*colsB + i] += A[j*colsA+k]*B[k*colsB + i];
            }
}

void print(double* mat, int rows, int columns) {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
            printf("%lf ", mat[i*columns + j]);
        }
        printf ("\n");
    }
    printf("\n");
}

int main(int argc, char** argv) {
    int size, rank, sizey, sizex, ranky, rankx;
    int dims[2] = {0,0}, periods[2] = {0,0}, coords[2];
    MPI_Comm comm2d;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    MPI_Dims_create(size, 2, dims);
    sizey = dims[0];
    sizex = dims[1];

    MPI_Cart_create(MPI_COMM_WORLD, 2, dims, periods,
                    1, &comm2d);

    MPI_Comm_rank(comm2d, &rank);

    MPI_Cart_get(comm2d, 2, dims, periods, coords);
    ranky = coords[0];
    rankx = coords[1];

    int remainDims[2] = {1, 0}; 
    MPI_Comm commColumns;
    MPI_Cart_sub(comm2d, remainDims, &commColumns);

    MPI_Comm commRows;
    remainDims[0] = 0;
    remainDims[1] = 1;
    MPI_Cart_sub(comm2d, remainDims, &commRows);



    double *A, *B;
    int columnsA, rowsA, columnsB, rowsB, columnsC, rowsC;
    if (rank == 0) {
        //scanf("%ud %ud %ud", &columnsA, &rowsA, &rowsB);
        columnsA = 400;
        rowsA = 400;
        rowsB = 400;

        A = calloc(columnsA*rowsA, sizeof(double));
        if (!A) {
            printf("Cannot allocate A");
            exit(1);
        }
        B = calloc(rowsA*rowsB, sizeof(double));
        if (!B) {
            printf("Cannot allocate B");
            exit(1);
        }

        for (int i = 0; i < columnsA*rowsA; i++) {
            //scanf("%lf", A + i);
            A[i] = 1;
        }

        for (int i = 0; i < rowsA*rowsB; i++) {
            //scanf("%lf", B + i);
            B[i] = 1;
        }
    }

    MPI_Bcast(&columnsA, 1, MPI_INT,
              0, MPI_COMM_WORLD);
    MPI_Bcast(&rowsA, 1, MPI_INT,
              0, MPI_COMM_WORLD);
    MPI_Bcast(&rowsB, 1, MPI_INT,
              0, MPI_COMM_WORLD);
    columnsB = rowsA;
    columnsC = rowsA;
    rowsC = columnsB;

    double start = MPI_Wtime();

    MPI_Datatype COLUMN;
    MPI_Datatype recvROW;
    MPI_Datatype sendROW;
    MPI_Datatype RESIZED_COLUMN;
    MPI_Datatype recvRESIZED_ROW;
    MPI_Datatype sendRESIZED_ROW; 
    
    MPI_Type_vector(rowsB, columnsB/sizex, columnsB,
                    MPI_DOUBLE, &COLUMN);
    MPI_Type_vector(rowsC/sizey, columnsC/sizex, columnsC,
                    MPI_DOUBLE, &recvROW);
    MPI_Type_contiguous(rowsC*columnsC/(sizex*sizey), MPI_DOUBLE, &sendROW);
    MPI_Type_commit(&COLUMN);
    MPI_Type_commit(&recvROW);
    MPI_Type_commit(&sendROW);
    

    MPI_Type_create_resized(COLUMN, 0,
                        columnsB/sizex * sizeof(double), &RESIZED_COLUMN);
    MPI_Type_create_resized(recvROW, 0,
                        columnsC/sizey * sizeof(double), &recvRESIZED_ROW);
    MPI_Type_create_resized(sendROW, 0, columnsC/sizey, &sendRESIZED_ROW);
    MPI_Type_commit(&RESIZED_COLUMN);
    MPI_Type_commit(&recvRESIZED_ROW);
    MPI_Type_commit(&sendRESIZED_ROW);

    

    size_t ApartSize = columnsA*rowsA/sizey;
    double* localA = calloc(ApartSize, sizeof(double));
    if (rankx == 0) {
        MPI_Scatter(A, ApartSize, MPI_DOUBLE, localA,
            ApartSize, MPI_DOUBLE, 0, commColumns);
    }
    MPI_Bcast(localA, ApartSize, MPI_DOUBLE, 0, commRows);

    size_t BpartSize = columnsB*rowsB/sizex;
    double* localB = calloc(BpartSize, sizeof(double));
    if (ranky == 0) {
        MPI_Scatter(B, 1, RESIZED_COLUMN, localB, 
            BpartSize, MPI_DOUBLE, 0, commRows);
    }
    MPI_Bcast(localB, BpartSize, MPI_DOUBLE, 0, commColumns);



    double* reslocal = calloc(rowsC*columnsC/(sizex*sizey),sizeof(double));
   
    multiplyMatricies(localA, localB, reslocal, 
            rowsC/sizey, columnsA, columnsC/sizex);



    double* gatheredRow = calloc(rowsC*columnsC/sizey, sizeof(double));
    MPI_Gather(reslocal,/*rowsA*columnsB/(sizex*sizey)*/1 , sendRESIZED_ROW,
                gatheredRow,  1, recvRESIZED_ROW, 0, commRows);
    
    double* result;
    if (rank == 0) {
        result = calloc(rowsA*columnsB, sizeof(double));
    }
    if (rankx == 0) {
        MPI_Gather(gatheredRow, rowsC*columnsC/sizey, MPI_DOUBLE, result,
                rowsC*columnsC/sizey, MPI_DOUBLE, 0, commColumns);
    }



    double end = MPI_Wtime();
    if (rank == 0) {
        printf("%lf\n", end - start);
        print(result, rowsC, columnsC);
    }

    MPI_Type_free(&COLUMN);
    MPI_Type_free(&sendROW);
    MPI_Type_free(&recvROW);
    MPI_Type_free(&RESIZED_COLUMN);
    MPI_Type_free(&sendRESIZED_ROW);
    MPI_Type_free(&recvRESIZED_ROW);

    free(gatheredRow);
    free(reslocal);
    free(localA);
    free(localB);
    if (rank == 0) {
        free(A);
        free(B);
        free(result);
    }

    MPI_Finalize();
    return 0;
}

