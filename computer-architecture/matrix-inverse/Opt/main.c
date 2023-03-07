#include <stdio.h>
#include <immintrin.h>
#include <stdlib.h>
#include <float.h>

typedef struct matrix {
    double* data;
    size_t size;
} matrix;

matrix* MultiplieMatricies(matrix* A, matrix* B)
{
    __m256i vindex = _mm256_set_epi64x(3*A->size, 2*A->size, 1*A->size, 0);
    for (size_t i = 0; i < A->size; i++)
    {
        for (size_t j = 0; j < A->size; j++)
        {
            double dot_product = 0;
            for (size_t k = 0; k < A->size/4; k++)
            {
                __m256d x = _mm256_loadu_pd(A->data + i * A->size + k*4);
                __m256d y = _mm256_i64gather_pd(B->data + k*4 * (B->size) + j, vindex, 8);

                __m256d prod = _mm256_mul_pd( x, y);
                __m256d halfsum = _mm256_hadd_pd(prod, prod);
                __m256d shuffle = _mm256_permute4x64_pd(halfsum, 0b11011000);
                dot_product += _mm256_cvtsd_f64(_mm256_hadd_pd(shuffle, shuffle));

            }
            for(size_t k = A->size & 0b11111111111111111111111111111100; k < A->size; k++)
            {
                dot_product += A->data[i * A->size + k]*B->data[k * B->size + j];
            }
            m->data[i*m->size + j] = dot_product;
        }
    }
    return m;
}

double hsum(__m256d v)
{
    __m256d halfsum = _mm256_hadd_pd(v, v);
    __m256d shuffle = _mm256_permute4x64_pd(halfsum, 0b11011000);
    return _mm256_cvtsd_f64(_mm256_hadd_pd(shuffle, shuffle));
}

double hmax(__m256d v)
{
    __m256d shuffle = _mm256_permute4x64_pd(v, 0b01001110);
    __m256d sh_max = _mm256_max_pd(v, shuffle);
    return _mm256_cvtsd_f64(_mm256_max_pd(sh_max, _mm256_permute4x64_pd(sh_max, 0b10110001)));
}

double Norm1(matrix* m)
{
    __m256i vindex = _mm256_set_epi64x(3*m->size, 2*m->size, 1*m->size, 0);
    double* sums = (double*) calloc(m->size, sizeof(double));
    for (size_t j = 0; j < m->size; j++)
    {
        for (size_t i = 0; i < m->size/4; i++) {
            sums[j] += hsum(_mm256_i64gather_pd(m->data + i* m->size*4 + j, vindex, 8));
        }

        for(size_t k = m->size & 0b11111111111111111111111111111100; k < m->size; k++)
        {
            sums[j] += m->data[k*m->size + j];
        }
    }
    double max = -DBL_MAX;
    for (size_t i = 0; i < m->size/4; i++)
    {
        double local_max = hmax(_mm256_loadu_pd(sums+i*4));
        max = max < local_max ? local_max : max;
    }
    for(size_t k = m->size & 0b11111111111111111111111111111100; k < m->size; k++)
    {
        max = max < m->data[k] ? m->data[k] : max;
    }
    return max;
}

double Norm2(matrix* m)
{
    double* sums = (double*) calloc(m->size, sizeof(double));
    for (size_t i = 0; i < m->size; i++)
    {
        for (size_t j = 0; j < m->size/4; j++) {
            sums[i] += hsum(_mm256_loadu_pd(m->data + i* m->size + j*4));
        }

        for(size_t k = m->size & 0b11111111111111111111111111111100; k < m->size; k++)
        {
            sums[i] += m->data[i*m->size + k];
        }
    }
    double max = -DBL_MAX;
    for (size_t i = 0; i < m->size/4; i++)
    {
        double local_max = hmax(_mm256_loadu_pd(sums+i*4));
        max = max < local_max ? local_max : max;
    }
    for(size_t k = m->size & 0b11111111111111111111111111111100; k < m->size; k++)
    {
        max = max < m->data[k] ? m->data[k] : max;
    }
    return max;
}

matrix* DivideMatrixByScalar(matrix* m, double scalar)
{
    __m256d scalar_vec = _mm256_set1_pd(scalar);
    for (size_t i = 0; i < m->size; i++)
    {
        for (size_t j = 0; j < m->size/4; j++) {
            __m256d resbuf = _mm256_loadu_pd(m->data + i*m->size + j*4);
            resbuf = _mm256_div_pd(resbuf, scalar_vec);
            _mm256_storeu_pd(res->data + i*res->size + j*4, resbuf);
        }

        for(size_t k = m->size & 0b11111111111111111111111111111100; k < m->size; k++)
        {
            __m256d resbuf = _mm256_load_pd(m->data + i*m->size + k);
            resbuf = _mm256_div_pd(resbuf, scalar_vec);
            _mm256_store_pd(res->data + i * res->size + k, resbuf);
        }
    }
    return res;
}

matrix* TransposeMatrix(matrix* A)
{
    matrix* res = (matrix*) malloc(sizeof(matrix));
    res->data = (double*) malloc(A->size * A->size * sizeof(double));
    res->size = A->size;

//    for (size_t i = 0; i < A->size/4; i++)
//    {
//
//        for (size_t j = i + 1; j < A->size/4; j++)
//        {
//            __m256d line1 = _mm256_loadu_pd(A->data + i*4 * A->size + j*4);
//            __m256d line2 = _mm256_loadu_pd(A->data + (i*4 + 1) * A->size + j*4);
//            __m256d line3 = _mm256_loadu_pd(A->data + (i*4 + 2) * A->size + j*4);
//            __m256d line4 = _mm256_loadu_pd(A->data + (i*4 + 3) * A->size + j*4);
//
//            _MM_TRANSPOSE4_PS()
//
//            _mm256_storeu_pd(res->data + i*4*res->size + j*4, line1);
//            _mm256_storeu_pd(res->data + (i*4 + 1) *res->size + j*4, line2);
//            _mm256_storeu_pd(res->data + (i*4 + 2) *res->size + j*4, line3);
//            _mm256_storeu_pd(res->data + (i*4 + 3) *res->size + j*4, line4);
//        }
//
//        for(size_t k = A->size & 0b11111111111111111111111111111100; k < A->size; k++)
//        {
//
//        }
//    }
    for (size_t i = 0; i < A->size; i++)
    {
        for (size_t j = 0; j < A->size; j++)
        {
            res->data[j*res->size + i] = A->data[i*A->size + j];
        }
    }

    return res;
}

void DestroyMatrix(matrix* m)
{
    free(m->data);
    free(m);
}

matrix* InverseMatrix(matrix* A, size_t N)
{
    matrix* res = (matrix*) malloc(sizeof(matrix));
    res->data = (double*) malloc(A->size * A->size * sizeof(double));
    res->size = A->size;

    matrix* I = GetEMatrix(A->size);
    matrix* At = TransposeMatrix(A);
    PrintMatrix(At);
    double scalar = Norm2(A) * Norm1(A);
    printf("%.2lf %.2lf %.2lf\n \n ", Norm1(A), Norm2(A), scalar);
    matrix* B = DivideMatrixByScalar(At, scalar);
    PrintMatrix(B);
    matrix* BA = MultiplieMatricies(B, A);
    PrintMatrix(BA);
    matrix* R = SubtractMatricies(I, BA);
    PrintMatrix(R);
    matrix* sum = GetEMatrix(A->size);
    matrix* Rn = GetEMatrix(A->size);
    matrix* t_p = Rn;
    Rn = MultiplieMatricies(t_p, R);
    matrix* t_s = sum;
    sum = AddMatricies(t_s, Rn);
    for (int i = 0; i < A->size; i++)
    {
        DestroyMatrix(t_p);
        t_p = Rn;
        Rn = MultiplieMatricies(t_p, R);
        DestroyMatrix(t_s);
        t_s = sum;
        sum = AddMatricies(t_s, Rn);
    }
    DestroyMatrix(BA);
    BA = MultiplieMatricies(sum, B);

    DestroyMatrix(I);
    DestroyMatrix(At);
    DestroyMatrix(B);
    DestroyMatrix(R);
    DestroyMatrix(sum);
    DestroyMatrix(Rn);

    return BA;
}

int main()
{
    size_t N;
    scanf("%lu", &N);
    matrix* m = GetMatrix(N);

    PrintMatrix(m);
    scanf("%lu", &N);
    matrix* inv = InverseMatrix(m, N);
    PrintMatrix(inv);

    free(inv);
    free(m);
    return 0;
}
