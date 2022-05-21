
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <openmpi-x86_64/mpi.h>
#include <stdlib.h>
#define CALC_LIMIT 1000
#define PRINT_LIMIT 10

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

double mulScalar(const double* l_vec, const double* r_vec, int size) {
    double mulResult = 0;
    for (int i = 0; i < size; i++) {
        mulResult += l_vec[i] * r_vec[i];
    }
    return mulResult;
}

void setEquations(double* a_mat, double* b_vec, int size) {
    double u_vec[size];

    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (i != j)
                a_mat[i * size + j] = (cos(i) + cos(j))/2;
            else
                a_mat[i * size + j] = cos(i) + size;
        }
        u_vec[i] = sin(i);
    }

    if (size <= PRINT_LIMIT) {
        puts("Required solution");
        print(u_vec, 1, size);
    }

    mulMat(a_mat, u_vec, b_vec, size, size, 1);
}

int main(int argc, char* argv[]) {
    //Инициализация MPI
    int rank;
    int pcount;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &pcount);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    double starttime, endtime;

    //Установка размерности СЛУ и точности вычисления
    int size,
            error = 0;
    double accuracy;
    if (rank == 0) {
        puts("Enter required SLE size:");
        error = scanf("%d", &size);
    }
    MPI_Bcast(&size, 1, MPI_INT, 0, MPI_COMM_WORLD);
    if (rank == 0) {
        puts("Enter required accuracy:");
        error = scanf("%lf", &accuracy);
    }
    MPI_Bcast(&error, 1, MPI_INT, 0, MPI_COMM_WORLD);
    if (error != 1) {
        if (rank == 0) puts("Incorrect input");
        MPI_Finalize();
        return 0;
    }
    MPI_Bcast(&accuracy, 1, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    //Выделение памяти под основные вектора и матрицу
    double *a_mat;
    double *x_vec = (double*)malloc(size * sizeof(double));
    double *b_vec = (double*)malloc(size * sizeof(double));

    //Установка СЛУ
    if (rank == 0) {
        a_mat = (double*)malloc(size * size * sizeof(double));

        setEquations(a_mat, b_vec, size);

        if (size <= PRINT_LIMIT) {
            puts("Matrix A");
            print(a_mat, size, size);
            puts("Vector B");
            print(b_vec, 1, size);
        }
    }

    //Начало замера
    starttime = MPI_Wtime();

    //Начальное состояние вектора x
    if (rank == 0) {
        for (int i = 0; i < size; i++)
            x_vec[i] = b_vec[i];
    }

    //Данные для раздачи матрицы
    int lines_per_proc = size / pcount;
    int num_of_extra_lines = size % pcount;
    int *A_part_sizes = (int*)malloc(pcount * sizeof(int));
    int *displs_A = (int*)malloc(pcount * sizeof(int));
    int displace_A = 0;
    for (int i = 0; i < pcount; i++) {
        if (i < num_of_extra_lines) {
            A_part_sizes[i] = (lines_per_proc + 1) * size;
            displs_A[i] = displace_A;
            displace_A += A_part_sizes[i];
        }
        else {
            A_part_sizes[i] = lines_per_proc * size;
            displs_A[i] = displace_A;
            displace_A += A_part_sizes[i];
        }
    }
    double *A_part = (double*)malloc(A_part_sizes[rank] * sizeof(double));
    int A_part_size = A_part_sizes[rank] / size;

    //Данные для передачи векторов
    int *displs_vec = (int*)malloc(pcount * sizeof(int));
    int *vec_part_sizes = (int*)malloc(pcount * sizeof(int));
    for (int i = 0; i < pcount; i++) {
        displs_vec[i] = displs_A[i] / size;
        vec_part_sizes[i] = A_part_sizes[i] / size;
    }
    int vec_part_size = vec_part_sizes[rank];
    double add_val;

    //Делим матрицу и раздаем векторы
    MPI_Scatterv(a_mat, A_part_sizes, displs_A, MPI_DOUBLE, A_part, A_part_sizes[rank], MPI_DOUBLE, 0, MPI_COMM_WORLD);
    MPI_Bcast(b_vec, size, MPI_DOUBLE, 0, MPI_COMM_WORLD);
    MPI_Bcast(x_vec, size, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    //Характеристики проверки условия
    int solved;
    double high_scalar;
    double low_scalar;
    double norm_b;
    int safe_count = 0;

    //Характеристики итеративного вычисления
    double t;
    double* y_vec = (double*)malloc(size * sizeof(double));
    double* y_part = (double*)malloc(vec_part_sizes[rank] * sizeof(double));
    double* ay_part = (double*)malloc(vec_part_sizes[rank] * sizeof(double));

    //Первичная проверка выполнения условия
    //y = ax
    mulMat(A_part, x_vec, y_part, A_part_size, size, 1);
    //y = ax - b
    subMat(y_part, b_vec + displs_vec[rank], 1, vec_part_size);
    //(Ay, Ay)
    add_val = mulScalar(y_part, y_part, vec_part_size);
    MPI_Allreduce(&add_val, &high_scalar, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
    //(b, b)
    add_val = 0;
    for (int i = 0; i < vec_part_size; i++) {
        add_val += b_vec[displs_vec[rank] + i] * b_vec[displs_vec[rank] + i];
    }
    MPI_Allreduce(&add_val, &norm_b, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
    //(Ay, Ay)/(b, b) < e^2
    solved = ((high_scalar / norm_b) < accuracy);

    //Основное вычисление
    while (!solved && safe_count < CALC_LIMIT) {
        MPI_Allgatherv(y_part, vec_part_size, MPI_DOUBLE, y_vec, vec_part_sizes, displs_vec, MPI_DOUBLE, MPI_COMM_WORLD);

        //Ay
        mulMat(A_part, y_vec, ay_part, A_part_size, size, 1);

        //t = (Ay, y)/(Ay, Ay)
        add_val = mulScalar(y_part, ay_part, vec_part_size);
        MPI_Allreduce(&add_val, &high_scalar, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
        add_val = mulScalar(ay_part, ay_part, vec_part_size);
        MPI_Allreduce(&add_val, &low_scalar, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
        t = high_scalar / low_scalar;

        //y = ty
        mulByNum(y_part, t, 1, vec_part_size);
        MPI_Allgatherv(y_part, vec_part_size, MPI_DOUBLE, y_vec, vec_part_sizes, displs_vec, MPI_DOUBLE, MPI_COMM_WORLD);
        //x = x - y
        subMat(x_vec, y_vec, 1, size);

        //Проверка выполнения условия
        //y = ax
        mulMat(A_part, x_vec, y_part, A_part_size, size, 1);
        //y = ax - b
        for (int i = 0; i < vec_part_size; i++) {
            y_part[i] -= b_vec[displs_vec[rank] + i];
        }
        //(Ay, Ay)
        add_val = mulScalar(y_part, y_part, vec_part_size);
        MPI_Allreduce(&add_val, &high_scalar, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
        //(Ay, Ay)/(b, b) < e^2
        solved = ((high_scalar / norm_b) < accuracy);

        //Подсчёт итераций
        safe_count++;
        if (safe_count > CALC_LIMIT) {
            puts("Iterations limit exceeded");
            break;
        }
    }

    //Конец замера X
    endtime = MPI_Wtime();

    //Вывод X
    if (rank == 0) {
        if (size <= PRINT_LIMIT) {
            puts("Vector X");
            print(x_vec, 1, size);
        }

        puts("Time elapsed:");
        printf("%f\n", endtime - starttime);

        puts("Number of iterations:");
        printf("%d\n", safe_count);
    }

    MPI_Finalize();

    return 0;
}