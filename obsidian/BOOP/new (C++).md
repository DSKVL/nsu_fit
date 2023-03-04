## new (C++)
Ключевое слово в C++.
Хорошим тоном является написание new и после [[delete (C++)]]

#### Оператор
Оператор C++, вызывающий [[malloc]] или другую [[Переопределение операторов (C++)]] выделяющую память функцию. 

Может выделять больше памяти, чем [[malloc]], например, при создании массивов записыавет размер. Причина, по которой необходимо вызывать [[delete (C++)]] с [].

Есть несколько вариантов, noexcept (MyClass \*p = new(nothrow MyClass();) и throws bad_alloc.

#### Создание объекта
При создании объекта вызывается оператор new, затем [[Конструктор]]



#Cpp 