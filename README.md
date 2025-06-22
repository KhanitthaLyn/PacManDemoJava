🌟PacManDemoJava

There still have many features that I havent finished it yet!, also not clean code...

This code is part of a Pac-Man style game implemented using Java Swing. It manages rendering and controlling characters: Pac-Man, Ghosts, and Cherry.

I created by 

1️⃣Initial Setup

➡︎ rowCount, columnCount, tileSize: Define the grid size

➡︎ map[]: Represents the map structure using characters

➡︎ 'X' = wall

➡︎ ' ' = empty/food

➡︎ 'P' = Pac-Man starting point

➡︎ 'b','o','p','r' = various ghost starting positions


2️⃣ Block Class

➡︎ Represents movable/static game objects. Each Block has x/y position, dimensions, image, direction, and velocity.

3️⃣ Movement

➡︎ Pac-Man: Controlled by keyboard input

➡︎ Ghosts: Move automatically and randomly, only valid directions (non-wall)

➡︎ Cherry: Moves randomly and slowly along food path

➡︎ Function getValidDirs(Block b) returns valid directions for movement based on current grid and wall layout.

4️⃣ Collision Detection

➡︎ Function collision(Block a, Block b) checks for overlaps:

➡︎ Colliding with ghosts: lose a life

➡︎ Colliding with cherry: gain score

5️⃣ Rendering

➡︎ Handled in paintComponent(Graphics g), which draws:

Walls, food dots, Pac-Man, ghosts, cherry

➡︎ Status text: score, lives, elapsed time
