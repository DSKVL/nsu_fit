#include <iostream>
#include "stack.h"

int main() {

    stack<int> st{};
    int a = 1;
    st.put(a);

    int b = st.top();

    return 0;
}
