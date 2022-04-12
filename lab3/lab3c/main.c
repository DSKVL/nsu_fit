#include <openmpi-x86_64/mpi.h>
#include <malloc.h>
#include <string.h>

typedef struct shareinfo {
    int* sendCountsMatrix;
    int* displacesMatrix;
    int *displacesVectors;
    int *sendCountsVectors;
    int CurrentVectorPartSize;
    int currentMatrixPartSize;
} shareinfo_t;

void print(double* matrix, int row_count, int column_count) {
    for (int i = 0; i < row_count; i++) {
        for (int j = 0; j < column_count; j++)
            printf("%.6f ", matrix[i * column_count + j]);
        putchar('\n');
    }
}

void mulMat(const double* l_mat, const double* r_mat, double* out_mat, int N, int M, int K) {
    memset(out_mat, 0, N * K * sizeof(double));
    for (int i = 0; i < N; i++)
        for (int h = 0; h < M; h++)
            for (int j = 0; j < K; j++)
                out_mat[i * K + j] += l_mat[i * M + h] * r_mat[h * K + j];
}

void subMat(double* l_mat, const double* r_mat, int row_count, int column_count) {
    for (int i = 0; i < row_count; i++)
        for (int j = 0; j < column_count; j++)
            l_mat[i * column_count + j] -= r_mat[i * column_count + j];
}

void mulByNum(double* mat, double co, int row_count, int column_count) {
    for (int i = 0; i < row_count; i++)
        for (int j = 0; j < column_count; j++)
            mat[i * column_count + j] *= co;
}

double dotProduct(const double* lVec, const double* rVec, int size) {
    double mulResult = 0;
    for (int i = 0; i < size; i++) {
        mulResult += lVec[i] * rVec[i];
    }
    return mulResult;
}

shareinfo_t getShareInfo(int dim, int rank, int size){
    shareinfo_t res = { (int*) malloc(size * sizeof(int)),
                        (int*) malloc(size * sizeof(int)),
                        (int*) malloc (size * sizeof(int)),
                        (int*) malloc (size * sizeof(int)),
                        };

    int linesPerProcess = dim/size;
    int extraLines = dim%size;
    int absoluteDisplace = 0;

    for (int i = 0; i < extraLines; i++) {
        res.sendCountsMatrix[i] = (linesPerProcess + 1) * dim;
        res.displacesMatrix[i] = absoluteDisplace * dim;
        res.sendCountsVectors[i] = (linesPerProcess + 1);
        res.displacesVectors[i] = absoluteDisplace;
        absoluteDisplace += res.sendCountsVectors[i];

    }
    for (int i = extraLines; i < size; i++) {
        res.sendCountsMatrix[i] = (linesPerProcess) * dim;
        res.displacesMatrix[i] = absoluteDisplace* dim;
        res.sendCountsVectors[i] = (linesPerProcess);
        res.displacesVectors[i] = absoluteDisplace;
        absoluteDisplace += res.sendCountsVectors[i];
    }

    res.CurrentVectorPartSize = res.sendCountsVectors[rank];
    res.currentMatrixPartSize = res.sendCountsMatrix[rank];
    return res;
}

void shareProblem(double* A, double* CurrentPartA, double* x, double* b, int dim,
                  shareinfo_t* info) {
    MPI_Scatterv(A, info->sendCountsMatrix, info->displacesMatrix, MPI_DOUBLE, CurrentPartA,
                 info->currentMatrixPartSize, MPI_DOUBLE, 0, MPI_COMM_WORLD);
    MPI_Bcast(b, dim, MPI_DOUBLE, 0, MPI_COMM_WORLD);
    MPI_Bcast(x, dim
              , MPI_DOUBLE, 0, MPI_COMM_WORLD);
}

int solved(double* CurrentPartA, double* x, double* b, double* yPart, int dim, shareinfo_t info,int rank) {
    printf("in solved beggining\n");

    double scalarPart = 0;
    double upperScalar = 0;
    double normB = 0;

    mulMat(CurrentPartA, x, yPart, info.currentMatrixPartSize, dim, 1);
    subMat(yPart, b + info.displacesMatrix[rank], 1, info.CurrentVectorPartSize);

    printf("in solved before job\n");
//    scalarPart = dotProduct(yPart, yPart, info.CurrentVectorPartSize);
    scalarPart = 0;
    for (int i = 0; i < info.CurrentVectorPartSize; i++) {
        scalarPart += yPart[i] * yPart[i];
    }
    printf("in solved first dot pr\n");

    MPI_Allreduce(&scalarPart, &upperScalar, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
    printf("in solved first allreduce\n");

    scalarPart = 0;
    for (int i = 0; i < info.CurrentVectorPartSize; i++) {
        scalarPart += b[info.displacesVectors[rank] + i] * b[info.displacesVectors[rank] + i];
    }
    printf("in solved second dot pr\n");

    MPI_Allreduce(&scalarPart, &normB, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
    double crit = (upperScalar / normB);
    printf("%lf\n", crit);
    printf("in solved after job");

    return  (upperScalar / normB) < 0.000001;
}


void solve(double* CurrentPartA, double* x, double* b, int dim, shareinfo_t info, int rank) {
    double scalarPart = 0;
    double upperScalar = 0;
    double lowerScalar = 0;
    double t;
    double* y = (double*) malloc(dim * sizeof(double));
    double* yPart = (double*) malloc(info.CurrentVectorPartSize * sizeof(double));
    double* AyPart = (double*) malloc(info.CurrentVectorPartSize * sizeof(double));

    printf("in solve before while\n");

    while (!solved(CurrentPartA, x, b, yPart, dim, info, rank)) {
        printf("in while loop beggining\n");

        MPI_Allgatherv(yPart, info.CurrentVectorPartSize, MPI_DOUBLE, y,
                       info.sendCountsVectors, info.displacesVectors, MPI_DOUBLE, MPI_COMM_WORLD);

        mulMat(CurrentPartA, y, AyPart, info.currentMatrixPartSize, dim, 1);

        scalarPart = dotProduct(yPart, AyPart, info.CurrentVectorPartSize);
        MPI_Allreduce(&scalarPart, &upperScalar, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);

        scalarPart = dotProduct(AyPart, AyPart, info.CurrentVectorPartSize);
        MPI_Allreduce(&scalarPart, &lowerScalar, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
        t = upperScalar / lowerScalar;

        mulByNum(yPart, t, 1, info.CurrentVectorPartSize);
        MPI_Allgatherv(yPart, info.CurrentVectorPartSize, MPI_DOUBLE,
                       y, info.sendCountsVectors, info.displacesVectors,
                       MPI_DOUBLE, MPI_COMM_WORLD);

        subMat(x, y, 1, dim);
    }
}

double* HeatMatrixInit(size_t dim) {
    double* A = malloc(dim*dim * sizeof(double));
    for (size_t i = 0; i < dim; i++)
        for(size_t j = 0; j < dim; j++) {
            if (i==j) A[i*dim + j] = -4.0;
            else if ((i == j + dim || j == i + dim) || (i-j == 1 || i-j == -1) &&
                        (((i+1)%dim != 1) || ((j+1)%dim != 0)) && ((i+1)%dim!=0 || (j+1)%dim != 1))
                A[i*dim+ j] = 1.0;
        }
    return A;
}

int main(int argc, char **argv) {
    int size, rank;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    double timeStart, timeEnd;
    double *A, *x, *b;

    //TODO 50*50
    int dim = 2*2;

    x = (double*)malloc(dim * sizeof(double));
    b = (double*)malloc(dim * sizeof(double));
    if (rank ==0) {
        A = HeatMatrixInit(dim);
        print(A, dim, dim);
        b[0] = -9.0;
        b[1] = 0;
        b[2] = 0;
        b[3] = 0;
        x[0] = 0; x[1] = 0; x[2] = 0; x[3] = 0;
        //b[7 * dim + 7] = 9.0;
    }

    timeStart = MPI_Wtime();

    shareinfo_t shareInfo = getShareInfo(dim, rank, size);
    double* CurrentPartA = (double*) malloc (shareInfo.currentMatrixPartSize * sizeof(double));
    shareProblem(A, CurrentPartA, x, b, dim, &shareInfo);

    solve(CurrentPartA, x, b, dim, shareInfo, rank);

    timeEnd = MPI_Wtime();

    if (rank == 0)
        printf("%lf\n", timeEnd-timeStart);

    MPI_Finalize();
    return 0;
}
