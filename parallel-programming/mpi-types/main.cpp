#include <mpi.h>
#include <iostream>
#include <vector>
#include <numeric>

//умножение для уже транспонированной матрицы В
void multiplyMatricies(const double *A, const double *B, double *C,
                       size_t rowsA, size_t colsA, size_t colsB) {
    for (size_t i = 0; i < rowsA; i++)
        for (size_t j = 0; j < colsB; j++)
            for (size_t k = 0; k < colsA; k++)
                C[i * colsB + j] += A[i * colsA + k] * B[j * colsA + k];
}

void print(double *mat, int rows, int columns) {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++)
            std::cout << mat[i * columns + j] << " ";
        std::cout << "\n";
    }
    std::cout << "\n";
}

//заполняем матрицы
void setMatricies(std::vector<double>& A, std::vector<double>& B,
                  int &columnsA, int &rowsA, int &columnsB) {
    rowsA = 9, columnsA = 8, columnsB = 10;

    A = std::vector<double>(columnsA * rowsA);
    for (int i = 0; i < columnsA * rowsA; i++)
        A[i] = rand() % 10;
    B = std::vector<double>(columnsB * columnsA);
    for (int i = 0; i < columnsB * columnsA; i++)
        B[i] = rand() % 10;
}

//возвращает вектор, в котором на итой позиции стоит количество "элементов" для итого процесса
std::vector<int> getSendcounts(int dataSize, int processNumber, int multiplier=1) {
    auto sendcounts = std::vector<int>(); // количество элементов на группу
    auto extraData = dataSize % processNumber; // количество широких столбцов/строк поширше
    auto commonData = dataSize / processNumber; // минимальная ширина/выстоа

    for (int i = 0 ; i < extraData; i++)
        sendcounts.push_back((commonData + 1) * multiplier);
    for (int i = extraData; i < processNumber; i++)
        sendcounts.push_back(commonData * multiplier);
    return sendcounts;
}

//возвращает вектор, в котором на итой позиции стоит сдвижка
std::vector<int> getDisplaces(const std::vector<int> &sendcounts) {
    auto displaces = sendcounts;
    std::exclusive_scan(sendcounts.begin(), sendcounts.end(),
                        displaces.begin(), 0);
    return displaces;
}

int main(int argc, char **argv) {
    using namespace MPI;

    Init(argc, argv);
    auto size = COMM_WORLD.Get_size();      // записывает в size количество выделенных процессов

    int dims[]{0, 0};
    Compute_dims(size, 2, dims);     // определение размеров будущей "решётки" коммуникатора comm2d
    const auto sizey = dims[0];
    const auto sizex = dims[1];

    bool periods[]{false, false};       //создание коммуникатора comm2d с декартовой топологией
    auto comm2d = COMM_WORLD.Create_cart(2, dims, periods, true);
    auto rank = COMM_WORLD.Get_rank();     //получение своего номера в comm2d

    int coords[2];                             //получение своих координат в двумерной решётке коммуникатора comm2d
    comm2d.Get_topo(2, dims, periods, coords);
    const auto ranky = coords[0];
    const auto rankx = coords[1];

    bool remainDimsVertical[]{true, false};     //создание подгрупп commColumns
    auto commColumns = comm2d.Sub(remainDimsVertical);
    bool remainDimsHorizontal[]{false, true};   //создание подгрупп commRows
    auto commRows = comm2d.Sub(remainDimsHorizontal);

    std::vector<double> A, B;
    int columnsA, rowsA, columnsB, rowsB, columnsC, rowsC;
    if (rank == 0)
        setMatricies(A, B, columnsA, rowsA, columnsB);

    COMM_WORLD.Bcast(&columnsA, 1, INT, 0);           // тут всё в терминах оригинальной матрицы (ячеечки)
    COMM_WORLD.Bcast(&rowsA, 1, INT, 0);
    COMM_WORLD.Bcast(&columnsB, 1, INT, 0);
    rowsB = columnsA;
    columnsC = columnsB;
    rowsC = rowsA;

    if (rank == 0) {
        print(A.data(), rowsA, columnsA);
        std::cout << "\n\n";
        print(B.data(), rowsB, columnsB);
        std::cout << "\n\n";
    }

    double start = Wtime();

    auto sendcountsX = getSendcounts(columnsB, sizex);       // получаем ширину "колонок"
    auto displacesX = getDisplaces(sendcountsX);                        // получаем координаты начала колонки
    auto sendcountsY = getSendcounts(rowsA, sizey);          // получаем высоту строк
    auto sendcountsYtimesColumnsA = getSendcounts(rowsA, sizey, columnsA);      // колчество эл-тов в строке
    auto displacesY = getDisplaces(sendcountsYtimesColumnsA);           // получаем координаты начала строки

    auto widthCurColumn = sendcountsX[rankx];
    auto heightCurRow = sendcountsY[ranky];

    Datatype COLUMN = DOUBLE.Create_vector(rowsB, 1, columnsB);
    Datatype RESIZED_COLUMN = COLUMN.Create_resized(0, 1 * sizeof(double));
    RESIZED_COLUMN.Commit();

    Datatype sendColumn = DOUBLE.Create_vector(heightCurRow, 1, widthCurColumn);   // столбец из reslocal
    Datatype sendRESIZED_COLUMN = sendColumn.Create_resized(0, 1 * sizeof(double));
    sendRESIZED_COLUMN.Commit();

    Datatype recvColumn = DOUBLE.Create_vector(heightCurRow, 1, columnsC);         // столбец в gatheredRow
    Datatype recvRESIZED_COLUMN = recvColumn.Create_resized(0, 1 * sizeof(double));
    recvRESIZED_COLUMN.Commit();


    int ApartSize = heightCurRow * columnsA;
    auto localA = std::vector<double>(ApartSize);
    if (rankx == 0)                                                                         // в каждый поток отправили свою "строку" из А
        commColumns.Scatterv(A.data(), sendcountsYtimesColumnsA.data(), displacesY.data(), DOUBLE,
                             localA.data(), ApartSize, DOUBLE, 0);
    commRows.Bcast(localA.data(), ApartSize, DOUBLE, 0);


    int BpartSize = widthCurColumn * rowsB;
    auto localB = std::vector<double>(BpartSize);
    if (ranky == 0)                                                                         // в каждый поток отправили свой "столбец" из В
        commRows.Scatterv(B.data(), sendcountsX.data(), displacesX.data(), RESIZED_COLUMN, // !тут происходит неявное транспонирование!
                          localB.data(), BpartSize, DOUBLE, 0);
    commColumns.Bcast(localB.data(), BpartSize, DOUBLE, 0);

    size_t reslocalsize = heightCurRow * widthCurColumn;
    auto reslocal = std::vector<double>(reslocalsize);                                  // в reslocal - результат перемножения "строки" и "столбца"

    multiplyMatricies(localA.data(), localB.data(), reslocal.data(),
                      heightCurRow, columnsA, widthCurColumn);

    int gatheredRowSize = heightCurRow * columnsC;
    auto gatheredRow = std::vector<double>();
    auto sendcountsRes = getSendcounts(rowsA, sizey, columnsC);
    auto displacesRes = getDisplaces(sendcountsRes);
    auto result = std::vector<double>();
    if (rankx == 0)
        gatheredRow = std::vector<double>(gatheredRowSize);
    if (rank == 0)
        result = std::vector<double>(rowsC * columnsC);

    //сбор строк
    commRows.Gatherv(reslocal.data(), widthCurColumn, sendRESIZED_COLUMN,
                     gatheredRow.data(), sendcountsX.data(), displacesX.data(),
                     recvRESIZED_COLUMN, 0);

    //сбор столбца
    if (rankx == 0)
        commColumns.Gatherv(gatheredRow.data(), gatheredRowSize, DOUBLE,
                            result.data(), sendcountsRes.data(), displacesRes.data(),
                            DOUBLE, 0);

    double end = Wtime();

    if (rank == 0) {
        std::cout << end - start << "\n";
        print(result.data(), rowsC, columnsC);
    }

    COLUMN.Free();
    RESIZED_COLUMN.Free();
    sendColumn.Free();
    sendRESIZED_COLUMN.Free();
    recvColumn.Free();
    recvRESIZED_COLUMN.Free();

    Finalize();
    return 0;
}
