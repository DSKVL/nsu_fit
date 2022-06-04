#include <iostream>
#include <ncurses.h>
#include <unistd.h>

#define DELAY 30000

int main(int argc, char *argv[]) {
    int x = 0, y = 0;
    int maxy = 0, maxx = 0;


    initscr();
    noecho();
    curs_set(FALSE);

    while (1) {
        getmaxyx(stdscr, maxy, maxx);
        x = x % maxx;
        y = y % maxy;
        clear(); // Clear the screen of all
        // previously-printed characters
        mvprintw(y, x, "o"); // Print our "ball" at the current xy position
        refresh();

        usleep(DELAY); // Shorter delay between movements
        x++;
        y++;// Advance the ball to the right
    }

    endwin();
}