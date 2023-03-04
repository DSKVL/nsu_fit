def compile [source] {
  if ($source | str ends-with '.c') {
    let executableLength = ($source | str length) - 2
    let executable = ($source | str rpad -l $executableLength -c '0');
    gcc -Wall -Wextra -pedantic -Werror -Wconversion -fsanitize=address $source -o $executable
  } else {
    echo 'Not a .c file'
  }
}

def compilePthread [source] {
  if ($source | str ends-with '.c') {
    let executableLength = ($source | str length) - 2
    let executable = ($source | str rpad -l $executableLength -c '0');
    gcc -Wall -Wextra -pedantic -Werror -Wconversion -fsanitize=address -pthread $source -o $executable
  } else {
    echo 'Not a .c file'
  }
}
