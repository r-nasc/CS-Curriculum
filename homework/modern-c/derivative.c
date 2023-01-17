/* C program for numerical derivatives */
#include <stdio.h>
#include <math.h>

int close(double a, double b) {
    return fabs(a - b) < 1.0e-6;
}

double derivative(double (f)(double), double x) {
    const double d = 1.0e-6;
    double x1 = x - d, x2 = x + d;
    double y1 = f(x1), y2 = f(x2);
    return (y2 - y1) / (x2 - x1);
}

int main() {
    int test1_result = close(derivative(sin, 0.0), 1.0);
    printf("Test1: %s\n", test1_result ? "Success" : "Failure");

    int test2_result = close(derivative(sin, M_PI / 2.0), 0.0);
    printf("Test2: %s\n", test2_result ? "Success" : "Failure");

    return 0;
}
