# Stochastic Game of Life

This repository contains a simple java applet which presents a twist on [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life). In the classic version the following rules apply:

1. Any live cell with fewer than two live neighbours dies, as if caused by under-population.
2. Any live cell with two or three live neighbours lives on to the next generation.
3. Any live cell with more than three live neighbours dies, as if by over-population.
4. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

In this version the rules can be changed slightly. We allow a revival rate which causes some cells to randomly come back to life (this can be controlled within the applet). In addition to this we allow the rules to be mutated according to a markov chain - of which the user controls the rate of transition.

---

This project is a result of me playing around with GUIs in java and wanting to create and extend Conway's Game of Life.
